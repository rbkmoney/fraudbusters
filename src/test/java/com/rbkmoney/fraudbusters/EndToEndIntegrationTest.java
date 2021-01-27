package com.rbkmoney.fraudbusters;

import com.rbkmoney.clickhouse.initializer.ChInitializer;
import com.rbkmoney.damsel.domain.RiskScore;
import com.rbkmoney.damsel.fraudbusters.*;
import com.rbkmoney.damsel.proxy_inspector.Context;
import com.rbkmoney.damsel.proxy_inspector.InspectorProxySrv;
import com.rbkmoney.fraudbusters.repository.impl.ChargebackRepository;
import com.rbkmoney.fraudbusters.repository.impl.PaymentRepositoryImpl;
import com.rbkmoney.fraudbusters.repository.impl.RefundRepository;
import com.rbkmoney.woody.thrift.impl.http.THClientBuilder;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.thrift.TException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.kafka.test.rule.EmbeddedKafkaRule;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.testcontainers.containers.ClickHouseContainer;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import static com.rbkmoney.fraudbusters.util.BeanUtil.*;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;


@Slf4j
@RunWith(SpringRunner.class)
@ActiveProfiles("full-prod")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@SpringBootTest(webEnvironment = RANDOM_PORT, classes = FraudBustersApplication.class,
        properties = {"kafka.listen.result.concurrency=1", "kafka.historical.listener.enable=true"})
@ContextConfiguration(initializers = EndToEndIntegrationTest.Initializer.class)
public class EndToEndIntegrationTest extends IntegrationTest {

    private static final String TEMPLATE =
            "rule:TEMPLATE: count(\"email\", 10, 0, \"party_id\", \"shop_id\") > 1  AND count(\"email\", 10) < 3 " +
                    "AND sum(\"email\", 10, \"party_id\", \"shop_id\") >= 18000 " +
                    "AND countSuccess(\"card_token\", 10, \"party_id\", \"shop_id\") > 1 " +
                    "AND in(countryBy(\"country_bank\"), \"RUS\") " +
                    "OR sumRefund(\"card_token\", 10, \"party_id\", \"shop_id\") > 0 " +
                    "OR countRefund(\"card_token\", 10, \"party_id\", \"shop_id\") > 0 " +
                    "OR countChargeback(\"card_token\", 10, \"party_id\", \"shop_id\") > 0 " +
                    "OR sumChargeback(\"card_token\", 10, \"party_id\", \"shop_id\") > 0 \n" +
                    " -> declineAndNotify;";

    private static final String TEMPLATE_CONCRETE =
            "rule:TEMPLATE_CONCRETE:  sumSuccess(\"email\", 10) >= 29000  -> decline;";

    private static final String GROUP_DECLINE =
            "rule:GROUP_DECLINE:  1 >= 0  -> decline;";

    private static final String GROUP_NORMAL =
            "rule:GROUP_NORMAL:  1 < 0  -> decline;";

    private static final String TEMPLATE_CONCRETE_SHOP =
            "rule:TEMPLATE_CONCRETE_SHOP:  sum(\"email\", 10) >= 18000 and not isTrusted()  -> accept;";

    private static final String P_ID = "test";
    private static final String GROUP_P_ID = "group_1";
    public static final String CAPTURED = "captured";
    public static final String PROCESSED = "processed";
    public static final String FAILED = "failed";

    @Autowired
    PaymentRepositoryImpl paymentRepository;

    @Autowired
    ChargebackRepository chargebackRepository;

    @Autowired
    RefundRepository refundRepository;

    @Autowired
    JdbcTemplate jdbcTemplate;

    @LocalServerPort
    int serverPort;

    private static String SERVICE_URL = "http://localhost:%s/fraud_inspector/v1";

    @ClassRule
    public static EmbeddedKafkaRule kafka = createKafka();

    @ClassRule
    public static ClickHouseContainer clickHouseContainer = new ClickHouseContainer("yandex/clickhouse-server:19.17");

    @Override
    protected String getBrokersAsString() {
        return kafka.getEmbeddedKafka().getBrokersAsString();
    }

    public static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        @SneakyThrows
        @Override
        public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
            log.info("clickhouse.db.url={}", clickHouseContainer.getJdbcUrl());
            log.info("kafka.bootstrap.servers={}", kafka.getEmbeddedKafka().getBrokersAsString());
            TestPropertyValues.of("clickhouse.db.url=" + clickHouseContainer.getJdbcUrl(),
                    "clickhouse.db.user=" + clickHouseContainer.getUsername(),
                    "clickhouse.db.password=" + clickHouseContainer.getPassword(),
                    "kafka.bootstrap.servers=" + kafka.getEmbeddedKafka().getBrokersAsString())
                    .applyTo(configurableApplicationContext.getEnvironment());
            ChInitializer.initAllScripts(clickHouseContainer, List.of("sql/db_init.sql",
                    "sql/V2__create_events_p2p.sql",
                    "sql/V3__create_fraud_payments.sql",
                    "sql/V4__create_payment.sql",
                    "sql/V5__add_fields.sql",
                    "sql/V6__add_result_fields_payment.sql",
                    "sql/V7__add_fields.sql"));
        }
    }

    @Before
    public void init() throws ExecutionException, InterruptedException, TException {
        String globalRef = UUID.randomUUID().toString();
        produceTemplate(globalRef, TEMPLATE, kafkaTopics.getFullTemplate());
        produceReference(true, null, null, globalRef);

        String partyTemplate = UUID.randomUUID().toString();
        produceTemplate(partyTemplate, TEMPLATE_CONCRETE, kafkaTopics.getFullTemplate());
        produceReference(false, P_ID, null, partyTemplate);

        String shopRef = UUID.randomUUID().toString();
        produceTemplate(shopRef, TEMPLATE_CONCRETE_SHOP, kafkaTopics.getFullTemplate());
        produceReference(false, P_ID, ID_VALUE_SHOP, shopRef);

        String groupTemplateDecline = UUID.randomUUID().toString();
        produceTemplate(groupTemplateDecline, GROUP_DECLINE, kafkaTopics.getFullTemplate());
        String groupTemplateNormal = UUID.randomUUID().toString();
        produceTemplate(groupTemplateNormal, GROUP_NORMAL, kafkaTopics.getFullTemplate());

        String groupId = UUID.randomUUID().toString();
        produceGroup(groupId, List.of(new PriorityId()
                .setId(groupTemplateDecline)
                .setPriority(2L), new PriorityId()
                .setId(groupTemplateNormal)
                .setPriority(1L)), kafkaTopics.getFullGroupList());
        produceGroupReference(GROUP_P_ID, null, groupId);
        Mockito.when(geoIpServiceSrv.getLocationIsoCode(any())).thenReturn("RUS");

    }

    @Test
    public void test() throws URISyntaxException, TException, InterruptedException {
        waitingTopic(kafkaTopics.getTemplate());
        waitingTopic(kafkaTopics.getGroupList());
        waitingTopic(kafkaTopics.getReference());
        waitingTopic(kafkaTopics.getGroupReference());

        testFraudRules();

        testValidation();
    }

    private void testFraudRules() throws URISyntaxException, InterruptedException, TException {
        THClientBuilder clientBuilder = new THClientBuilder()
                .withAddress(new URI(String.format(SERVICE_URL, serverPort)))
                .withNetworkTimeout(300000);
        InspectorProxySrv.Iface client = clientBuilder.build(InspectorProxySrv.Iface.class);

        Thread.sleep(TIMEOUT);

        Context context = createContext();
        RiskScore riskScore = client.inspectPayment(context);
        Assert.assertEquals(RiskScore.high, riskScore);

        paymentRepository.insertBatch(List.of(convertContextToPayment(context, PROCESSED)));
        paymentRepository.insertBatch(List.of(convertContextToPayment(context, CAPTURED)));

        context = createContext();
        riskScore = client.inspectPayment(context);
        Assert.assertEquals(RiskScore.fatal, riskScore);

        paymentRepository.insertBatch(List.of(convertContextToPayment(context, FAILED)));

        context = createContext(P_ID);
        riskScore = client.inspectPayment(context);
        Assert.assertEquals(RiskScore.low, riskScore);

        paymentRepository.insertBatch(List.of(convertContextToPayment(context, PROCESSED)));
        paymentRepository.insertBatch(List.of(convertContextToPayment(context, CAPTURED)));

        //test groups templates
        context = createContext(GROUP_P_ID);
        riskScore = client.inspectPayment(context);
        Assert.assertEquals(RiskScore.fatal, riskScore);

        //test chargeback functions
        String chargeTest = "charge-test";
        context = createContext(chargeTest);
        context.getPayment().getShop().setId(chargeTest);
        context.getPayment().getParty().setPartyId(chargeTest);
        riskScore = client.inspectPayment(context);
        Assert.assertEquals(RiskScore.high, riskScore);

        chargebackRepository.insertBatch(List.of(convertContextToChargeback(context, ChargebackStatus.accepted.name())));

        riskScore = client.inspectPayment(context);

        Assert.assertEquals(RiskScore.fatal, riskScore);

        //test refund functions
        String refundShopId = "refund-test";
        context.getPayment().getShop().setId(refundShopId);
        riskScore = client.inspectPayment(context);
        Assert.assertEquals(RiskScore.high, riskScore);

        refundRepository.insertBatch(List.of(convertContextToRefund(context, RefundStatus.failed.name())));

        riskScore = client.inspectPayment(context);
        Assert.assertEquals(RiskScore.high, riskScore);

        refundRepository.insertBatch(List.of(convertContextToRefund(context, RefundStatus.succeeded.name())));

        riskScore = client.inspectPayment(context);
        Assert.assertEquals(RiskScore.fatal, riskScore);
    }

    public void testValidation() throws URISyntaxException, TException {
        THClientBuilder clientBuilder = new THClientBuilder()
                .withAddress(new URI(String.format("http://localhost:%s/fraud_payment_validator/v1/", serverPort)))
                .withNetworkTimeout(300000);
        PaymentServiceSrv.Iface client = clientBuilder.build(PaymentServiceSrv.Iface.class);

        ValidateTemplateResponse validateTemplateResponse = client.validateCompilationTemplate(
                List.of(new Template()
                        .setId("dfsdf")
                        .setTemplate(TEMPLATE.getBytes()))
        );

        Assert.assertTrue(validateTemplateResponse.getErrors().isEmpty());
    }

}
