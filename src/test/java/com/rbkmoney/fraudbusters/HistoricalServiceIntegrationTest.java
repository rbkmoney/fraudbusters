package com.rbkmoney.fraudbusters;

import com.rbkmoney.clickhouse.initializer.ChInitializer;
import com.rbkmoney.damsel.fraudbusters.ClientInfo;
import com.rbkmoney.damsel.fraudbusters.HistoricalTransactionCheck;
import com.rbkmoney.damsel.fraudbusters.MerchantInfo;
import com.rbkmoney.damsel.fraudbusters.PaymentInfo;
import com.rbkmoney.damsel.fraudbusters.Template;
import com.rbkmoney.fraudbusters.service.HistoricalDataServiceImpl;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.kafka.test.rule.EmbeddedKafkaRule;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.testcontainers.containers.ClickHouseContainer;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@Slf4j
@RunWith(SpringRunner.class)
@ActiveProfiles("full-prod")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@SpringBootTest(webEnvironment = RANDOM_PORT, classes = FraudBustersApplication.class,
        properties = {"kafka.listen.result.concurrency=1", "kafka.historical.listener.enable=true"})
@ContextConfiguration(initializers = HistoricalServiceIntegrationTest.Initializer.class)
public class HistoricalServiceIntegrationTest extends IntegrationTest {
    @ClassRule
    public static EmbeddedKafkaRule kafka = createKafka();

    @ClassRule
    public static ClickHouseContainer clickHouseContainer = new ClickHouseContainer("yandex/clickhouse-server:19.17");

    @Autowired
    private HistoricalDataServiceImpl historicalDataService;

    private static final String TEMPLATE_ID = UUID.randomUUID().toString();
    private static final String TEMPLATE = "rule: amount() > 10 \n" +
            "-> accept;";

    @Override
    protected String getBrokersAsString() {
        return kafka.getEmbeddedKafka().getBrokersAsString();
    }

    @Test
    public void applyOneRuleOnly() {
        Template template = new Template();
        template.setId(TEMPLATE_ID);
        template.setTemplate(TEMPLATE.getBytes(StandardCharsets.UTF_8));

        PaymentInfo bigAmountTransaction = new PaymentInfo();
        bigAmountTransaction.setId(UUID.randomUUID().toString());
        bigAmountTransaction.setAmount(25L);
        bigAmountTransaction.setCurrency("RUB");
        bigAmountTransaction.setCardToken("card_token");
        bigAmountTransaction.setEventTime(LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));
        MerchantInfo merchantInfo = new MerchantInfo();
        merchantInfo.setPartyId(UUID.randomUUID().toString());
        merchantInfo.setShopId(UUID.randomUUID().toString());
        bigAmountTransaction.setMerchantInfo(merchantInfo);
        ClientInfo clientInfo = new ClientInfo();
        clientInfo.setEmail("test@email.org");
        clientInfo.setIp("127.0.0.1");
        clientInfo.setFingerprint(UUID.randomUUID().toString());
        bigAmountTransaction.setClientInfo(clientInfo);

        PaymentInfo smallAmountTransaction = new PaymentInfo(bigAmountTransaction);
        smallAmountTransaction.setAmount(2L);

        Set<HistoricalTransactionCheck> historicalTransactionChecks =
                historicalDataService.applySingleRule(template, Set.of(bigAmountTransaction, smallAmountTransaction));

        assertEquals(2, historicalTransactionChecks.size());
        HistoricalTransactionCheck acceptedCheck =
                findInSetByPaymentInfo(historicalTransactionChecks, bigAmountTransaction);
        assertTrue(acceptedCheck.getCheckResult().getConcreteCheckResult().getResultStatus().isSetAccept());

        HistoricalTransactionCheck notAcceptedCheck =
                findInSetByPaymentInfo(historicalTransactionChecks, smallAmountTransaction);
        assertNull(notAcceptedCheck.getCheckResult().getConcreteCheckResult());
    }

    private HistoricalTransactionCheck findInSetByPaymentInfo(
            Set<HistoricalTransactionCheck> checks,
            PaymentInfo paymentInfo
    ) {
        return checks.stream()
                .filter(check -> check.getTransaction().equals(paymentInfo))
                .findFirst()
                .orElseThrow();
    }

    public static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        @SneakyThrows
        @Override
        public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
            log.info("clickhouse.db.url={}", clickHouseContainer.getJdbcUrl());
            log.info("kafka.bootstrap.servers={}", kafka.getEmbeddedKafka().getBrokersAsString());
            TestPropertyValues.of(
                    "clickhouse.db.url=" + clickHouseContainer.getJdbcUrl(),
                    "clickhouse.db.user=" + clickHouseContainer.getUsername(),
                    "clickhouse.db.password=" + clickHouseContainer.getPassword(),
                    "kafka.bootstrap.servers=" + kafka.getEmbeddedKafka().getBrokersAsString()
            )
                    .applyTo(configurableApplicationContext.getEnvironment());
            ChInitializer.initAllScripts(clickHouseContainer, List.of(
                    "sql/db_init.sql",
                    "sql/V2__create_events_p2p.sql",
                    "sql/V3__create_fraud_payments.sql",
                    "sql/V4__create_payment.sql",
                    "sql/V5__add_fields.sql",
                    "sql/V6__add_result_fields_payment.sql",
                    "sql/V7__add_fields.sql",
                    "sql/V8__create_withdrawal.sql"
            ));
        }
    }
}
