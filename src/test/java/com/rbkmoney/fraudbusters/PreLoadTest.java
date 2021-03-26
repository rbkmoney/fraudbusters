package com.rbkmoney.fraudbusters;

import com.rbkmoney.clickhouse.initializer.ChInitializer;
import com.rbkmoney.damsel.domain.RiskScore;
import com.rbkmoney.damsel.proxy_inspector.Context;
import com.rbkmoney.damsel.proxy_inspector.InspectorProxySrv;
import com.rbkmoney.fraudbusters.repository.impl.FraudResultRepository;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
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
import ru.yandex.clickhouse.ClickHouseDataSource;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;


@Slf4j
@RunWith(SpringRunner.class)
@ActiveProfiles("full-prod")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@SpringBootTest(webEnvironment = RANDOM_PORT, classes = FraudBustersApplication.class,
        properties = {"kafka.listen.result.concurrency=1", "kafka.historical.listener.enable=true"})
@ContextConfiguration(initializers = PreLoadTest.Initializer.class)
public class PreLoadTest extends IntegrationTest {

    private static final String TEMPLATE = "rule: 12 >= 1\n" +
                                           " -> accept;";
    private static final String TEST = "test";

    @ClassRule
    public static EmbeddedKafkaRule kafka = createKafka();
    @ClassRule
    public static ClickHouseContainer clickHouseContainer = new ClickHouseContainer("yandex/clickhouse-server:19.17");

    @MockBean
    ClickHouseDataSource clickHouseDataSource;
    @Autowired
    JdbcTemplate jdbcTemplate;
    @MockBean
    FraudResultRepository paymentRepository;

    @LocalServerPort
    int serverPort;

    private InspectorProxySrv.Iface client;

    @Override
    protected String getBrokersAsString() {
        return kafka.getEmbeddedKafka().getBrokersAsString();
    }

    @Before
    public void init() throws ExecutionException, InterruptedException {
        produceTemplate(TEST, TEMPLATE, kafkaTopics.getFullTemplate());
        produceReferenceWithWait(true, null, null, TEST, 10);
        waitingTopic(kafkaTopics.getFullTemplate());
        waitingTopic(kafkaTopics.getFullReference());
    }

    @Test
    public void inspectPaymentTest() throws URISyntaxException, TException {
        waitingTopic(kafkaTopics.getTemplate());
        waitingTopic(kafkaTopics.getReference());

        THClientBuilder clientBuilder = new THClientBuilder()
                .withAddress(new URI(String.format("http://localhost:%s/fraud_inspector/v1", serverPort)))
                .withNetworkTimeout(300000);
        client = clientBuilder.build(InspectorProxySrv.Iface.class);

        Context context = BeanUtil.createContext();
        RiskScore riskScore = client.inspectPayment(context);

        Assert.assertEquals(RiskScore.low, riskScore);
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
                    "sql/V7__add_fields.sql"
            ));
        }
    }

}
