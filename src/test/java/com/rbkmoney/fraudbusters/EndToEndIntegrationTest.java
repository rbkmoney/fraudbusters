package com.rbkmoney.fraudbusters;

import com.rbkmoney.damsel.domain.RiskScore;
import com.rbkmoney.damsel.fraudbusters.*;
import com.rbkmoney.damsel.proxy_inspector.Context;
import com.rbkmoney.damsel.proxy_inspector.InspectorProxySrv;
import com.rbkmoney.fraudbusters.factory.TestObjectsFactory;
import com.rbkmoney.fraudbusters.pool.HistoricalPool;
import com.rbkmoney.fraudbusters.repository.clickhouse.impl.ChargebackRepository;
import com.rbkmoney.fraudbusters.repository.clickhouse.impl.PaymentRepositoryImpl;
import com.rbkmoney.fraudbusters.repository.clickhouse.impl.RefundRepository;
import com.rbkmoney.trusted.tokens.ConditionTemplate;
import com.rbkmoney.woody.thrift.impl.http.THClientBuilder;
import com.rbkmoney.woody.thrift.impl.http.THSpawnClientBuilder;
import lombok.extern.slf4j.Slf4j;
import org.antlr.v4.runtime.ParserRuleContext;
import org.apache.thrift.TException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import static com.rbkmoney.fraudbusters.util.BeanUtil.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@Slf4j
@ActiveProfiles("full-prod")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@SpringBootTest(webEnvironment = RANDOM_PORT, classes = FraudBustersApplication.class,
        properties = {"kafka.listen.result.concurrency=1", "kafka.historical.listener.enable=true"})
class EndToEndIntegrationTest extends JUnit5IntegrationTest {

    public static final String CAPTURED = "captured";
    public static final String PROCESSED = "processed";
    public static final String FAILED = "failed";

    private static final String TEMPLATE = """
            rule:TEMPLATE: count("email", 10, 0, "party_id", "shop_id") > 1
              AND count("email", 10) < 3
              AND sum("email", 10, "party_id", "shop_id") >= 18000
              AND countSuccess("card_token", 10, "party_id", "shop_id") > 1
              AND in(countryBy("country_bank"), "RUS")
              OR sumRefund("card_token", 10, "party_id", "shop_id") > 0
              OR countRefund("card_token", 10, "party_id", "shop_id") > 0
              OR countChargeback("card_token", 10, "party_id", "shop_id") > 0
              OR sumChargeback("card_token", 10, "party_id", "shop_id") > 0
             -> declineAndNotify;
            """;

    private static final String TEMPLATE_CONCRETE =
            "rule:TEMPLATE_CONCRETE:  sumSuccess(\"email\", 10) >= 29000  -> decline;";
    private static final String GROUP_DECLINE =
            "rule:GROUP_DECLINE:  1 >= 0  -> decline;";
    private static final String GROUP_NORMAL =
            "rule:GROUP_NORMAL:  1 < 0  -> decline;";
    private static final String TEMPLATE_CONCRETE_SHOP = """
            rule:TEMPLATE_CONCRETE_SHOP:  sum("email", 10) >= 18000
             AND isTrusted(
                paymentsConditions(
                    condition("RUB",1,1000,10),
                    condition("EUR",2,20)
                ),
                withdrawalsConditions(
                    condition("USD",0,3000,3),
                    condition("CAD",2,4)
                )
             )
             -> accept;";
            """;
    private static final String P_ID = "test";
    private static final String GROUP_P_ID = "group_1";

    private static final String GLOBAL_REF = UUID.randomUUID().toString();
    private static final String PARTY_TEMPLATE = UUID.randomUUID().toString();
    private static final String SHOP_REF = UUID.randomUUID().toString();
    private static final String GROUP_TEMPLATE_DECLINE = UUID.randomUUID().toString();
    private static final String GROUP_TEMPLATE_NORMAL = UUID.randomUUID().toString();
    private static final String GROUP_ID = UUID.randomUUID().toString();

    private static final String FRAUD_INSPECTOR_SERVICE_URL = "http://localhost:%s/fraud_inspector/v1";
    private static final String HISTORICAL_SERVICE_URL = "http://localhost:%s/historical_data/v1/";

    @Autowired
    PaymentRepositoryImpl paymentRepository;
    @Autowired
    ChargebackRepository chargebackRepository;
    @Autowired
    RefundRepository refundRepository;
    @Autowired
    JdbcTemplate jdbcTemplate;
    @Autowired
    HistoricalPool<ParserRuleContext> timeTemplatePoolImpl;
    @Autowired
    private HistoricalPool<List<String>> timeGroupPoolImpl;
    @Autowired
    private HistoricalPool<String> timeReferencePoolImpl;
    @Autowired
    private HistoricalPool<String> timeGroupReferencePoolImpl;
    @LocalServerPort
    int serverPort;

    @BeforeEach
    public void init() throws ExecutionException, InterruptedException, TException {
        produceTemplate(GLOBAL_REF, TEMPLATE, kafkaTopics.getFullTemplate());
        produceReference(true, null, null, GLOBAL_REF);

        produceTemplate(PARTY_TEMPLATE, TEMPLATE_CONCRETE, kafkaTopics.getFullTemplate());
        produceReference(false, P_ID, null, PARTY_TEMPLATE);

        produceTemplate(SHOP_REF, TEMPLATE_CONCRETE_SHOP, kafkaTopics.getFullTemplate());
        produceReference(false, P_ID, ID_VALUE_SHOP, SHOP_REF);

        produceTemplate(GROUP_TEMPLATE_DECLINE, GROUP_DECLINE, kafkaTopics.getFullTemplate());
        produceTemplate(GROUP_TEMPLATE_NORMAL, GROUP_NORMAL, kafkaTopics.getFullTemplate());

        produceGroup(GROUP_ID, List.of(new PriorityId()
                .setId(GROUP_TEMPLATE_DECLINE)
                .setPriority(2L), new PriorityId()
                .setId(GROUP_TEMPLATE_NORMAL)
                .setPriority(1L)), kafkaTopics.getFullGroupList());
        produceGroupReference(GROUP_P_ID, null, GROUP_ID);
        Mockito.when(geoIpServiceSrv.getLocationIsoCode(any())).thenReturn("RUS");
        Mockito.when(trustedTokensSrv.isTokenTrusted(anyString(), any(ConditionTemplate.class))).thenReturn(true);
    }

    @Test
    public void test() throws URISyntaxException, TException, InterruptedException {
        waitingTopic(kafkaTopics.getTemplate());
        waitingTopic(kafkaTopics.getGroupList());
        waitingTopic(kafkaTopics.getReference());
        waitingTopic(kafkaTopics.getGroupReference());
        waitingTopic(kafkaTopics.getFullTemplate());

        testFraudRules();

        testValidation();

        testHistoricalPoolLinks();

        testApplyRuleOnHistoricalDataSets();
    }

    private void testFraudRules() throws URISyntaxException, InterruptedException, TException {
        THClientBuilder clientBuilder = new THClientBuilder()
                .withAddress(new URI(String.format(FRAUD_INSPECTOR_SERVICE_URL, serverPort)))
                .withNetworkTimeout(300000);
        InspectorProxySrv.Iface client = clientBuilder.build(InspectorProxySrv.Iface.class);

        Thread.sleep(TIMEOUT);

        Context context = createContext();
        RiskScore riskScore = client.inspectPayment(context);
        assertEquals(RiskScore.high, riskScore);

        paymentRepository.insertBatch(List.of(convertContextToPayment(context, PROCESSED)));
        paymentRepository.insertBatch(List.of(convertContextToPayment(context, CAPTURED)));

        context = createContext();
        riskScore = client.inspectPayment(context);
        assertEquals(RiskScore.fatal, riskScore);

        paymentRepository.insertBatch(List.of(convertContextToPayment(context, FAILED)));

        context = createContext(P_ID);
        riskScore = client.inspectPayment(context);
        assertEquals(RiskScore.low, riskScore);

        paymentRepository.insertBatch(List.of(convertContextToPayment(context, PROCESSED)));
        paymentRepository.insertBatch(List.of(convertContextToPayment(context, CAPTURED)));

        //test groups templates
        context = createContext(GROUP_P_ID);
        riskScore = client.inspectPayment(context);
        assertEquals(RiskScore.fatal, riskScore);

        //test chargeback functions
        String chargeTest = "charge-test";
        context = createContext(chargeTest);
        context.getPayment().getShop().setId(chargeTest);
        context.getPayment().getParty().setPartyId(chargeTest);
        riskScore = client.inspectPayment(context);
        assertEquals(RiskScore.high, riskScore);

        chargebackRepository.insertBatch(List.of(convertContextToChargeback(
                context,
                ChargebackStatus.accepted.name()
        )));

        riskScore = client.inspectPayment(context);

        assertEquals(RiskScore.fatal, riskScore);

        //test refund functions
        String refundShopId = "refund-test";
        context.getPayment().getShop().setId(refundShopId);
        riskScore = client.inspectPayment(context);
        assertEquals(RiskScore.high, riskScore);

        refundRepository.insertBatch(List.of(convertContextToRefund(context, RefundStatus.failed.name())));

        riskScore = client.inspectPayment(context);
        assertEquals(RiskScore.high, riskScore);

        refundRepository.insertBatch(List.of(convertContextToRefund(context, RefundStatus.succeeded.name())));

        riskScore = client.inspectPayment(context);
        assertEquals(RiskScore.fatal, riskScore);
    }

    private void testValidation() throws URISyntaxException, TException {
        THClientBuilder clientBuilder = new THClientBuilder()
                .withAddress(new URI(String.format("http://localhost:%s/fraud_payment_validator/v1/", serverPort)))
                .withNetworkTimeout(300000);
        PaymentServiceSrv.Iface client = clientBuilder.build(PaymentServiceSrv.Iface.class);

        ValidateTemplateResponse validateTemplateResponse = client.validateCompilationTemplate(
                List.of(new Template()
                        .setId("dfsdf")
                        .setTemplate(TEMPLATE.getBytes()))
        );

        assertTrue(validateTemplateResponse.getErrors().isEmpty());
    }

    private void testHistoricalPoolLinks() {
        assertEquals(5, timeTemplatePoolImpl.size());
        long timestamp = Instant.now().plus(Duration.ofDays(30)).toEpochMilli();
        assertNotNull(timeTemplatePoolImpl.get(GLOBAL_REF, timestamp));
        assertNotNull(timeTemplatePoolImpl.get(PARTY_TEMPLATE, timestamp));
        assertNotNull(timeTemplatePoolImpl.get(SHOP_REF, timestamp));
        assertNotNull(timeTemplatePoolImpl.get(GROUP_TEMPLATE_DECLINE, timestamp));
        assertNotNull(timeTemplatePoolImpl.get(GROUP_TEMPLATE_NORMAL, timestamp));

        String partyTemplateRefId = timeReferencePoolImpl.get(P_ID, timestamp);
        assertEquals(PARTY_TEMPLATE, partyTemplateRefId);
        assertNotNull(timeTemplatePoolImpl.get(partyTemplateRefId, timestamp));

        String groupRefId = timeGroupReferencePoolImpl.get(GROUP_P_ID, timestamp);
        assertEquals(GROUP_ID, groupRefId);
        List<String> groupTemplateIds = timeGroupPoolImpl.get(groupRefId, timestamp);
        for (String groupTemplateId : groupTemplateIds) {
            assertNotNull(timeTemplatePoolImpl.get(groupTemplateId, timestamp));
        }

        assertNull(timeTemplatePoolImpl.get(GLOBAL_REF, 0L));
        assertNull(timeTemplatePoolImpl.get(PARTY_TEMPLATE, 0L));
        assertNull(timeTemplatePoolImpl.get(SHOP_REF, 0L));
        assertNull(timeTemplatePoolImpl.get(GROUP_TEMPLATE_DECLINE, 0L));
        assertNull(timeTemplatePoolImpl.get(GROUP_TEMPLATE_NORMAL, 0L));
    }


    private void testApplyRuleOnHistoricalDataSets() throws URISyntaxException, TException {
        HistoricalDataServiceSrv.Iface client = new THSpawnClientBuilder()
                .withNetworkTimeout(30_000)
                .withAddress(new URI(String.format(HISTORICAL_SERVICE_URL, serverPort)))
                .build(HistoricalDataServiceSrv.Iface.class);

        testApplyRuleOnHistoricalDataSetsSingleRule(client);
        testApplyRuleOnHistoricalDataSetsWithinRuleSet(client);
    }

    private void testApplyRuleOnHistoricalDataSetsSingleRule(HistoricalDataServiceSrv.Iface client) throws TException {
        String rule = "rule: amount() < 1 -> decline;";
        Payment firstPayment = TestObjectsFactory.createPayment(10L);
        Payment secondPayment = TestObjectsFactory.createPayment(0L);
        HistoricalDataSetCheckResult result = client.applyRuleOnHistoricalDataSet(
                TestObjectsFactory.createEmulationRuleApplyRequest(
                        rule,
                        UUID.randomUUID().toString(),
                        firstPayment,
                        secondPayment)
        );
        assertEquals(2, result.getHistoricalTransactionCheck().size());
        var check = findHistoricalTransactionCheck(result.getHistoricalTransactionCheck(), firstPayment.getId());
        assertEquals(firstPayment, check.getTransaction());
        assertEquals(rule, check.getCheckResult().getCheckedTemplate());
        assertNull(check.getCheckResult().getConcreteCheckResult().getRuleChecked());
        assertEquals(new ArrayList<>(), check.getCheckResult().getConcreteCheckResult().getNotificationsRule());
        assertEquals(
                ResultStatus.normal(new Normal()),
                check.getCheckResult().getConcreteCheckResult().getResultStatus()
        );
        check = findHistoricalTransactionCheck(result.getHistoricalTransactionCheck(), secondPayment.getId());
        assertEquals(secondPayment, check.getTransaction());
        assertEquals(rule, check.getCheckResult().getCheckedTemplate());
        assertEquals("0", check.getCheckResult().getConcreteCheckResult().getRuleChecked());
        assertEquals(new ArrayList<>(), check.getCheckResult().getConcreteCheckResult().getNotificationsRule());
        assertEquals(
                ResultStatus.decline(new Decline()),
                check.getCheckResult().getConcreteCheckResult().getResultStatus()
        );

        String notifyRule = "rule: amount() < 1 -> notify;";
        result = client.applyRuleOnHistoricalDataSet(
                TestObjectsFactory.createEmulationRuleApplyRequest(
                        notifyRule,
                        UUID.randomUUID().toString(),
                        secondPayment)
        );
        check = findHistoricalTransactionCheck(result.getHistoricalTransactionCheck(), secondPayment.getId());
        assertEquals(secondPayment, check.getTransaction());
        assertEquals(notifyRule, check.getCheckResult().getCheckedTemplate());
        assertNull(check.getCheckResult().getConcreteCheckResult().getRuleChecked());
        assertEquals(List.of("0"), check.getCheckResult().getConcreteCheckResult().getNotificationsRule());
        assertEquals(
                ResultStatus.notify(new Notify()),
                check.getCheckResult().getConcreteCheckResult().getResultStatus()
        );
    }

    private void testApplyRuleOnHistoricalDataSetsWithinRuleSet(HistoricalDataServiceSrv.Iface client)
            throws TException {
        String rule = "rule: amount() < 1 -> decline;";
        Payment firstPayment = TestObjectsFactory.createPayment(10L, P_ID, SHOP_ID);
        Payment secondPayment = TestObjectsFactory.createPayment(0L, P_ID, SHOP_ID);
        HistoricalDataSetCheckResult result = client.applyRuleOnHistoricalDataSet(
                TestObjectsFactory.createCascadingEmulationRuleApplyRequest(
                        rule,
                        UUID.randomUUID().toString(),
                        P_ID,
                        null,
                        firstPayment,
                        secondPayment)
        );
        assertEquals(2, result.getHistoricalTransactionCheck().size());
        var check = findHistoricalTransactionCheck(result.getHistoricalTransactionCheck(), firstPayment.getId());
        assertEquals(firstPayment, check.getTransaction());
        assertEquals(rule, check.getCheckResult().getCheckedTemplate());
        assertNull(check.getCheckResult().getConcreteCheckResult().getRuleChecked());
        assertEquals(new ArrayList<>(), check.getCheckResult().getConcreteCheckResult().getNotificationsRule());
        assertEquals(
                ResultStatus.normal(new Normal()),
                check.getCheckResult().getConcreteCheckResult().getResultStatus()
        );
        check = findHistoricalTransactionCheck(result.getHistoricalTransactionCheck(), secondPayment.getId());
        assertEquals(secondPayment, check.getTransaction());
        assertEquals(rule, check.getCheckResult().getCheckedTemplate());
        assertEquals("0", check.getCheckResult().getConcreteCheckResult().getRuleChecked());
        assertEquals(new ArrayList<>(), check.getCheckResult().getConcreteCheckResult().getNotificationsRule());
        assertEquals(
                ResultStatus.decline(new Decline()),
                check.getCheckResult().getConcreteCheckResult().getResultStatus()
        );
    }

    private HistoricalTransactionCheck findHistoricalTransactionCheck(Set<HistoricalTransactionCheck> checks,
                                                                      String id) {
        return checks.stream()
                .filter(check -> check.getTransaction().getId().equals(id))
                .findFirst()
                .orElseThrow();
    }

}
