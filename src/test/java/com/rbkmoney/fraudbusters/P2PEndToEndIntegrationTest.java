package com.rbkmoney.fraudbusters;

import com.rbkmoney.clickhouse.initializer.ChInitializer;
import com.rbkmoney.damsel.domain.RiskScore;
import com.rbkmoney.damsel.p2p_insp.InspectResult;
import com.rbkmoney.fraudbusters.util.BeanUtil;
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
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.kafka.test.rule.EmbeddedKafkaRule;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.testcontainers.containers.ClickHouseContainer;
import ru.yandex.clickhouse.ClickHouseDataSource;
import ru.yandex.clickhouse.settings.ClickHouseProperties;

import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@Slf4j
@ActiveProfiles("full-prod")
@RunWith(SpringRunner.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@SpringBootTest(webEnvironment = RANDOM_PORT, classes = FraudBustersApplication.class, properties = "kafka.listen.result.concurrency=1")
@ContextConfiguration(initializers = P2PEndToEndIntegrationTest.Initializer.class)
public class P2PEndToEndIntegrationTest extends IntegrationTest {

    private static final String TEMPLATE =
            "rule: count(\"email\", 10, 0, \"identity_id\") > 1  AND count(\"email\", 10) < 3 " +
                    "AND sum(\"email\", 10) >= 18000 " +
                    "AND count(\"card_token_from\", 10) > 1 " +
                    "AND in(countryBy(\"country_bank\"), \"RUS\") \n" +
                    " -> decline;";

    public static final long TIMEOUT = 2000L;
    public static final String FRAUD = "fraud";
    public static final String IDENT_ID = "identId";

    @LocalServerPort
    int serverPort;

    private static final String SERVICE_P2P_URL = "http://localhost:%s/fraud_p2p_inspector/v1";

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
    public void init() throws ExecutionException, InterruptedException, SQLException, TException {

        String globalRef = UUID.randomUUID().toString();
        produceTemplate(globalRef, TEMPLATE, kafkaTopics.getP2pTemplate());
        produceP2PReference(true, null, globalRef);

        waitingTopic(kafkaTopics.getP2pTemplate());
        Mockito.when(geoIpServiceSrv.getLocationIsoCode(any())).thenReturn("RUS");

        Thread.sleep(TIMEOUT * 3);
    }

    @Test
    public void testP2P() throws URISyntaxException, TException, InterruptedException, ExecutionException, NoSuchFieldException, IllegalAccessException {
        THClientBuilder clientBuilder = new THClientBuilder()
                .withAddress(new URI(String.format(SERVICE_P2P_URL, serverPort)))
                .withNetworkTimeout(300000);

        com.rbkmoney.damsel.p2p_insp.InspectorProxySrv.Iface client = clientBuilder.build(com.rbkmoney.damsel.p2p_insp.InspectorProxySrv.Iface.class);
        com.rbkmoney.damsel.p2p_insp.Context p2PContext = BeanUtil.createP2PContext(IDENT_ID, "transfer_1");

        InspectResult inspectResult = client.inspectTransfer(p2PContext, List.of(FRAUD));
        Assert.assertEquals(RiskScore.high, inspectResult.scores.get(FRAUD));

        Thread.sleep(TIMEOUT);

        p2PContext = BeanUtil.createP2PContext(IDENT_ID, "transfer_1");
        inspectResult = client.inspectTransfer(p2PContext, List.of(FRAUD));
        Assert.assertEquals(RiskScore.fatal, inspectResult.scores.get(FRAUD));
    }

}
