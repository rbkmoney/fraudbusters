package com.rbkmoney.fraudbusters;

import com.rbkmoney.damsel.domain.RiskScore;
import com.rbkmoney.damsel.geo_ip.LocationInfo;
import com.rbkmoney.damsel.p2p_insp.InspectResult;
import com.rbkmoney.fraudbusters.serde.CommandDeserializer;
import com.rbkmoney.fraudbusters.util.BeanUtil;
import com.rbkmoney.fraudbusters.util.FileUtil;
import com.rbkmoney.woody.thrift.impl.http.THClientBuilder;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.thrift.TException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.rnorth.ducttape.unreliables.Unreliables;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.testcontainers.containers.ClickHouseContainer;
import ru.yandex.clickhouse.ClickHouseDataSource;
import ru.yandex.clickhouse.settings.ClickHouseProperties;

import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.Duration;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@Slf4j
@RunWith(SpringRunner.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@SpringBootTest(webEnvironment = RANDOM_PORT, classes = FraudBustersApplication.class, properties = "kafka.listen.result.concurrency=1")
@ContextConfiguration(initializers = P2PEndToEndIntegrationTest.Initializer.class)
public class P2PEndToEndIntegrationTest extends KafkaAbstractTest {

    private static final String TEMPLATE =
            "rule: count(\"email\", 10, 0, \"identity_id\") > 1  AND count(\"email\", 10) < 3 " +
                    "AND sum(\"email\", 10) >= 18000 " +
                    "AND count(\"card_token_from\", 10) > 1 " +
                    "AND in(countryBy(\"country_bank\"), \"RUS\") \n" +
                    " -> decline;";

    private static final int COUNTRY_GEO_ID = 12345;
    public static final long TIMEOUT = 2000L;
    public static final String FRAUD = "fraud";
    public static final String IDENT_ID = "identId";

    @LocalServerPort
    int serverPort;

    private static String SERVICE_P2P_URL = "http://localhost:%s/fraud_p2p_inspector/v1";

    @ClassRule
    public static ClickHouseContainer clickHouseContainer = new ClickHouseContainer("yandex/clickhouse-server:19.17");

    public static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        @Override
        public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
            log.info("clickhouse.db.url={}", clickHouseContainer.getJdbcUrl());
            TestPropertyValues.of("clickhouse.db.url=" + clickHouseContainer.getJdbcUrl(),
                    "clickhouse.db.user=" + clickHouseContainer.getUsername(),
                    "clickhouse.db.password=" + clickHouseContainer.getPassword())
                    .applyTo(configurableApplicationContext.getEnvironment());
            LocationInfo info = new LocationInfo();
            info.setCountryGeoId(COUNTRY_GEO_ID);
        }
    }

    private Connection getSystemConn() throws SQLException {
        ClickHouseProperties properties = new ClickHouseProperties();
        ClickHouseDataSource dataSource = new ClickHouseDataSource(clickHouseContainer.getJdbcUrl(), properties);
        return dataSource.getConnection();
    }

    @Before
    public void init() throws ExecutionException, InterruptedException, SQLException, TException {
        try (Connection connection = getSystemConn()) {
            String sql = FileUtil.getFile("sql/db_init.sql");
            String[] split = sql.split(";");
            for (String exec : split) {
                connection.createStatement().execute(exec);
            }

            sql = FileUtil.getFile("sql/V2__create_events_p2p.sql");
            split = sql.split(";");
            for (String exec : split) {
                connection.createStatement().execute(exec);
            }
        }

        String globalRef = UUID.randomUUID().toString();
        produceTemplate(globalRef, TEMPLATE, kafkaTopics.getP2pTemplate());
        produceP2PReference(true, null, globalRef);

        try (Consumer<String, Object> consumer = createConsumer(CommandDeserializer.class)) {
            consumer.subscribe(List.of(kafkaTopics.getP2pTemplate()));
            Unreliables.retryUntilTrue(10, TimeUnit.SECONDS, () -> {
                ConsumerRecords<String, Object> records = consumer.poll(Duration.ofSeconds(1));
                return records.isEmpty();
            });
        }

        Mockito.when(geoIpServiceSrv.getLocationIsoCode(any())).thenReturn("RUS");

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