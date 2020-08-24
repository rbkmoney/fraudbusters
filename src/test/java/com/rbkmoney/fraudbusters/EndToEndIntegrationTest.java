package com.rbkmoney.fraudbusters;

import com.rbkmoney.damsel.domain.RiskScore;
import com.rbkmoney.damsel.fraudbusters.PaymentServiceSrv;
import com.rbkmoney.damsel.fraudbusters.PriorityId;
import com.rbkmoney.damsel.fraudbusters.Template;
import com.rbkmoney.damsel.fraudbusters.ValidateTemplateResponse;
import com.rbkmoney.damsel.geo_ip.LocationInfo;
import com.rbkmoney.damsel.proxy_inspector.Context;
import com.rbkmoney.damsel.proxy_inspector.InspectorProxySrv;
import com.rbkmoney.fraudbusters.constant.ChargebackStatus;
import com.rbkmoney.fraudbusters.constant.RefundStatus;
import com.rbkmoney.fraudbusters.domain.Chargeback;
import com.rbkmoney.fraudbusters.domain.Payment;
import com.rbkmoney.fraudbusters.domain.Refund;
import com.rbkmoney.fraudbusters.repository.FraudPaymentRepositoryTest;
import com.rbkmoney.fraudbusters.repository.Repository;
import com.rbkmoney.fraudbusters.repository.impl.analytics.AnalyticsChargebackRepository;
import com.rbkmoney.fraudbusters.repository.impl.analytics.AnalyticsRefundRepository;
import com.rbkmoney.fraudbusters.util.ChInitializer;
import com.rbkmoney.fraudbusters.util.FileUtil;
import com.rbkmoney.woody.thrift.impl.http.THClientBuilder;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.thrift.TException;
import org.junit.*;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.event.annotation.AfterTestClass;
import org.springframework.test.context.junit4.SpringRunner;
import org.testcontainers.containers.ClickHouseContainer;
import org.testcontainers.containers.KafkaContainer;

import java.net.URI;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
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
public class EndToEndIntegrationTest extends KafkaAbstractTest {

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
            "rule:TEMPLATE_CONCRETE_SHOP:  sum(\"email\", 10) >= 18000  -> accept;";

    private static final int COUNTRY_GEO_ID = 12345;
    private static final String P_ID = "test";
    private static final String GROUP_P_ID = "group_1";
    public static final String CAPTURED = "captured";
    public static final String FAILED = "failed";

    @Autowired
    Repository<Payment> repository;

    @Autowired
    AnalyticsChargebackRepository analyticsChargebackRepository;

    @Autowired
    AnalyticsRefundRepository analyticsRefundRepository;

    @Autowired
    JdbcTemplate jdbcTemplate;

    @LocalServerPort
    int serverPort;

    private static String SERVICE_URL = "http://localhost:%s/fraud_inspector/v1";

    @ClassRule
    public static ClickHouseContainer clickHouseContainer = new ClickHouseContainer("yandex/clickhouse-server:19.17");

    @ClassRule
    public static KafkaContainer kafka = new KafkaContainer()
            .withEmbeddedZookeeper()
            .withCommand(FileUtil.getFile("kafka/kafka-test.sh"));

    @Override
    protected String getBootstrapServers() {
        return kafka.getBootstrapServers();
    }

    public static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        @SneakyThrows
        @Override
        public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
            log.info("clickhouse.db.url={}", clickHouseContainer.getJdbcUrl());
            TestPropertyValues.of("clickhouse.db.url=" + clickHouseContainer.getJdbcUrl(),
                    "clickhouse.db.user=" + clickHouseContainer.getUsername(),
                    "clickhouse.db.password=" + clickHouseContainer.getPassword(),
                    "kafka.bootstrap.servers=" + kafka.getBootstrapServers())
                    .applyTo(configurableApplicationContext.getEnvironment());
            LocationInfo info = new LocationInfo();
            info.setCountryGeoId(COUNTRY_GEO_ID);

            ChInitializer.initAllScripts(clickHouseContainer);
        }
    }

    @Before
    public void init() throws ExecutionException, InterruptedException, SQLException, TException {
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
        waitingTopic(kafkaTopics.getFullTemplate());

        String groupId = UUID.randomUUID().toString();
        produceGroup(groupId, List.of(new PriorityId()
                .setId(groupTemplateDecline)
                .setPriority(2L), new PriorityId()
                .setId(groupTemplateNormal)
                .setPriority(1L)), kafkaTopics.getFullGroupList());
        produceGroupReference(GROUP_P_ID, null, groupId);
        Mockito.when(geoIpServiceSrv.getLocationIsoCode(any())).thenReturn("RUS");

        waitingTopic(kafkaTopics.getGroupList());
        waitingTopic(kafkaTopics.getReference());
        waitingTopic(kafkaTopics.getGroupReference());
    }

    @Test
    public void test() throws URISyntaxException, TException, InterruptedException{
        THClientBuilder clientBuilder = new THClientBuilder()
                .withAddress(new URI(String.format(SERVICE_URL, serverPort)))
                .withNetworkTimeout(300000);
        InspectorProxySrv.Iface client = clientBuilder.build(InspectorProxySrv.Iface.class);

        Thread.sleep(TIMEOUT);

        Context context = createContext();
        RiskScore riskScore = client.inspectPayment(context);
        Assert.assertEquals(RiskScore.high, riskScore);

        repository.insert(convertContextToPayment(context, CAPTURED, new Payment()));

        context = createContext();
        riskScore = client.inspectPayment(context);
        Assert.assertEquals(RiskScore.fatal, riskScore);

        repository.insert(convertContextToPayment(context, FAILED, new Payment()));

        context = createContext(P_ID);
        riskScore = client.inspectPayment(context);
        Assert.assertEquals(RiskScore.low, riskScore);

        repository.insert(convertContextToPayment(context, CAPTURED, new Payment()));

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

        analyticsChargebackRepository.insert(convertContextToPayment(context, ChargebackStatus.accepted.name(), new Chargeback()));

        riskScore = client.inspectPayment(context);
        Assert.assertEquals(RiskScore.fatal, riskScore);

        //test refund functions
        String refundShopId = "refund-test";
        context.getPayment().getShop().setId(refundShopId);
        riskScore = client.inspectPayment(context);
        Assert.assertEquals(RiskScore.high, riskScore);

        analyticsRefundRepository.insert(convertContextToPayment(context, RefundStatus.failed.name(), new Refund()));

        riskScore = client.inspectPayment(context);
        Assert.assertEquals(RiskScore.high, riskScore);

        analyticsRefundRepository.insert(convertContextToPayment(context, RefundStatus.succeeded.name(), new Refund()));

        riskScore = client.inspectPayment(context);
        Assert.assertEquals(RiskScore.fatal, riskScore);

    }

    @Test
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

    @Test
    public void testFraudPayment() throws URISyntaxException, TException, InterruptedException {
        THClientBuilder clientBuilder = new THClientBuilder()
                .withAddress(new URI(String.format("http://localhost:%s/fraud_payment/v1/", serverPort)))
                .withNetworkTimeout(300000);
        PaymentServiceSrv.Iface client = clientBuilder.build(PaymentServiceSrv.Iface.class);

        //Payment
        client.insertFraudPayments(List.of(FraudPaymentRepositoryTest.createFraudPayment("inv")));
        Thread.sleep(TIMEOUT);

        List<Map<String, Object>> maps = jdbcTemplate.queryForList("SELECT * from fraud.fraud_payment");
        Assert.assertEquals(1, maps.size());
        Assert.assertEquals("kek@kek.ru", maps.get(0).get("email"));
    }

    @AfterTestClass
    public void afterTest(){
        kafka.stop();
    }

}