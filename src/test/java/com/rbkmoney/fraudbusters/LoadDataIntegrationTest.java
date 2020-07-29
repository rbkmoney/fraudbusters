package com.rbkmoney.fraudbusters;

import com.rbkmoney.damsel.fraudbusters.Payment;
import com.rbkmoney.damsel.fraudbusters.PaymentServiceSrv;
import com.rbkmoney.damsel.fraudbusters.PaymentStatus;
import com.rbkmoney.damsel.geo_ip.LocationInfo;
import com.rbkmoney.fraudbusters.constant.EventSource;
import com.rbkmoney.fraudbusters.domain.CheckedPayment;
import com.rbkmoney.fraudbusters.pool.HistoricalPool;
import com.rbkmoney.fraudbusters.repository.Repository;
import com.rbkmoney.fraudbusters.repository.impl.analytics.AnalyticsChargebackRepository;
import com.rbkmoney.fraudbusters.repository.impl.analytics.AnalyticsRefundRepository;
import com.rbkmoney.fraudbusters.serde.CommandDeserializer;
import com.rbkmoney.fraudbusters.util.ChInitializer;
import com.rbkmoney.fraudo.constant.ResultStatus;
import com.rbkmoney.woody.thrift.impl.http.THClientBuilder;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.antlr.v4.runtime.ParserRuleContext;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.thrift.TException;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.rnorth.ducttape.unreliables.Unreliables;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.testcontainers.containers.ClickHouseContainer;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import static com.rbkmoney.fraudbusters.util.BeanUtil.*;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@Slf4j
@RunWith(SpringRunner.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@SpringBootTest(webEnvironment = RANDOM_PORT, classes = FraudBustersApplication.class, properties = "kafka.listen.result.concurrency=1")
@ContextConfiguration(initializers = LoadDataIntegrationTest.Initializer.class)
public class LoadDataIntegrationTest extends KafkaAbstractTest {

    private static final String TEMPLATE =
            "rule:TEMPLATE: sum(\"card_token\", 1000, \"party_id\", \"shop_id\") > 0 " +
                    " and unique(\"email\", \"ip\", 1444) < 2 " +
                    " and count(\"card_token\", 1000, \"party_id\", \"shop_id\") > 5  -> decline";

    private static final String TEMPLATE_2 =
            "rule:TEMPLATE: count(\"card_token\", 1000, \"party_id\", \"shop_id\") > 2  -> decline;";

    private static final String TEMPLATE_CONCRETE =
            "rule:TEMPLATE_CONCRETE: count(\"card_token\", 10) > 0  -> accept;";

    private static final int COUNTRY_GEO_ID = 12345;
    public static final String PAYMENT_1 = "payment_1";
    public static final String PAYMENT_2 = "payment_2";
    public static final String PAYMENT_0 = "payment_0";

    private final String globalRef = UUID.randomUUID().toString();

    @Autowired
    Repository<CheckedPayment> repository;

    @Autowired
    AnalyticsChargebackRepository analyticsChargebackRepository;

    @Autowired
    AnalyticsRefundRepository analyticsRefundRepository;

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    HistoricalPool<ParserRuleContext> parserRuleContextTimePool;

    @LocalServerPort
    int serverPort;

    @ClassRule
    public static ClickHouseContainer clickHouseContainer = new ClickHouseContainer("yandex/clickhouse-server:19.17");

    public static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        @SneakyThrows
        @Override
        public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
            log.info("clickhouse.db.url={}", clickHouseContainer.getJdbcUrl());
            TestPropertyValues.of("clickhouse.db.url=" + clickHouseContainer.getJdbcUrl(),
                    "clickhouse.db.user=" + clickHouseContainer.getUsername(),
                    "clickhouse.db.password=" + clickHouseContainer.getPassword())
                    .applyTo(configurableApplicationContext.getEnvironment());
            LocationInfo info = new LocationInfo();
            info.setCountryGeoId(COUNTRY_GEO_ID);
            ChInitializer.initAllScripts(clickHouseContainer);
        }
    }

    @Before
    public void init() throws ExecutionException, InterruptedException, TException {
        produceTemplate(globalRef, TEMPLATE, kafkaTopics.getFullTemplate());
        produceReference(true, null, null, globalRef);
        try (Consumer<String, Object> consumer = createConsumer(CommandDeserializer.class)) {
            consumer.subscribe(List.of(kafkaTopics.getFullTemplate()));
            Unreliables.retryUntilTrue(10, TimeUnit.SECONDS, () -> {
                ConsumerRecords<String, Object> records = consumer.poll(100);
                return !records.isEmpty();
            });
        }
        Mockito.when(geoIpServiceSrv.getLocationIsoCode(any())).thenReturn("RUS");

        Thread.sleep(TIMEOUT * 3);
    }

    @Test
    @SneakyThrows
    public void testLoadData() {
        String oldTime = String.valueOf(LocalDateTime.now());
        produceTemplate(globalRef, TEMPLATE_2, kafkaTopics.getFullTemplate());
        Thread.sleep(TIMEOUT);

        THClientBuilder clientBuilder = new THClientBuilder()
                .withAddress(new URI(String.format("http://localhost:%s/fraud_payment_validator/v1/", serverPort)))
                .withNetworkTimeout(300000);
        PaymentServiceSrv.Iface client = clientBuilder.build(PaymentServiceSrv.Iface.class);

        checkInsertingBatch(client);

        Payment payment = createPayment(PaymentStatus.processed);
        payment.setId(PAYMENT_1);
        insertWithTimeout(client, List.of(payment));
        insertListDefaultPayments(client, PaymentStatus.captured, PaymentStatus.failed);
        checkPayment(PAYMENT_1, ResultStatus.DECLINE, 1);

        //check in past
        payment.setId(PAYMENT_0);
        payment.setEventTime(oldTime);
        insertWithTimeout(client, payment);
        checkPayment(PAYMENT_0, ResultStatus.THREE_DS, 1);

        String localId = UUID.randomUUID().toString();
        produceTemplate(localId, TEMPLATE_CONCRETE, kafkaTopics.getFullTemplate());
        produceReference(true, null, null, localId);
        Thread.sleep(TIMEOUT);

        payment.setId(PAYMENT_2);
        payment.setEventTime(String.valueOf(LocalDateTime.now()));
        insertWithTimeout(client, payment);
        checkPayment(PAYMENT_2, ResultStatus.ACCEPT, 1);

        //Chargeback
        client.insertChargebacks(List.of(createChargeback(com.rbkmoney.damsel.fraudbusters.ChargebackStatus.accepted),
                createChargeback(com.rbkmoney.damsel.fraudbusters.ChargebackStatus.cancelled)));
        Thread.sleep(TIMEOUT);

        List<Map<String, Object>> maps = jdbcTemplate.queryForList("SELECT * from " + EventSource.FRAUD_EVENTS_CHARGEBACK.getTable());
        assertEquals(2, maps.size());

        //Refund
        client.insertRefunds(List.of(createRefund(com.rbkmoney.damsel.fraudbusters.RefundStatus.succeeded),
                createRefund(com.rbkmoney.damsel.fraudbusters.RefundStatus.failed)));
        Thread.sleep(TIMEOUT);

        maps = jdbcTemplate.queryForList("SELECT * from " + EventSource.FRAUD_EVENTS_REFUND.getTable());
        assertEquals(2, maps.size());
    }

    private void checkInsertingBatch(PaymentServiceSrv.Iface client) throws TException, InterruptedException {
        insertWithTimeout(client, List.of(createPayment(PaymentStatus.processed), createPayment(PaymentStatus.processed), createPayment(PaymentStatus.processed), createPayment(PaymentStatus.processed), createPayment(PaymentStatus.processed)));
        List<Map<String, Object>> maps = jdbcTemplate.queryForList("SELECT * from " + EventSource.FRAUD_EVENTS_PAYMENT.getTable());
        assertEquals(5, maps.size());
        assertEquals("email", maps.get(0).get("email"));
        Thread.sleep(TIMEOUT);
    }

    private void insertWithTimeout(PaymentServiceSrv.Iface client, Payment payment) throws TException, InterruptedException {
        insertWithTimeout(client, List.of(payment));
    }

    private void insertWithTimeout(PaymentServiceSrv.Iface client, List<Payment> payments) throws TException, InterruptedException {
        client.insertPayments(payments);
        Thread.sleep(TIMEOUT);
    }

    private void checkPayment(String payment1, ResultStatus status, int expectedCount) {
        List<Map<String, Object>> maps = jdbcTemplate.queryForList(String.format("SELECT * from fraud.payment where id='%s'", payment1));
        log.info("SELECT : {}", maps);
        assertEquals(expectedCount, maps.size());
        assertEquals(status.name(), maps.get(0).get("resultStatus"));
    }

    private void insertListDefaultPayments(PaymentServiceSrv.Iface client, PaymentStatus processed, PaymentStatus processed2) throws TException, InterruptedException {
        insertWithTimeout(client, List.of(createPayment(processed), createPayment(processed2)));
    }

}