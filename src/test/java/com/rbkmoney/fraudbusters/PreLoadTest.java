package com.rbkmoney.fraudbusters;

import com.rbkmoney.damsel.domain.RiskScore;
import com.rbkmoney.damsel.proxy_inspector.Context;
import com.rbkmoney.damsel.proxy_inspector.InspectorProxySrv;
import com.rbkmoney.fraudbusters.repository.clickhouse.impl.FraudResultRepository;
import com.rbkmoney.fraudbusters.util.BeanUtil;
import com.rbkmoney.woody.thrift.impl.http.THClientBuilder;
import lombok.extern.slf4j.Slf4j;
import org.apache.thrift.TException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import ru.yandex.clickhouse.ClickHouseDataSource;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@Slf4j
@ActiveProfiles("full-prod")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@SpringBootTest(webEnvironment = RANDOM_PORT, classes = FraudBustersApplication.class,
        properties = {"kafka.listen.result.concurrency=1", "kafka.historical.listener.enable=true"})
public class PreLoadTest extends JUnit5IntegrationTest {

    private static final String TEMPLATE = "rule: 12 >= 1\n" +
                                           " -> accept;";
    private static final String TEST = "test";

    @MockBean
    ClickHouseDataSource clickHouseDataSource;
    @Autowired
    JdbcTemplate jdbcTemplate;
    @MockBean
    FraudResultRepository paymentRepository;

    @LocalServerPort
    int serverPort;

    private InspectorProxySrv.Iface client;

    @BeforeEach
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

        assertEquals(RiskScore.low, riskScore);
    }

}
