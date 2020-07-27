package com.rbkmoney.fraudbusters;

import com.rbkmoney.damsel.domain.RiskScore;
import com.rbkmoney.damsel.fraudbusters.Command;
import com.rbkmoney.damsel.fraudbusters.CommandBody;
import com.rbkmoney.damsel.fraudbusters.Template;
import com.rbkmoney.damsel.proxy_inspector.Context;
import com.rbkmoney.damsel.proxy_inspector.InspectorProxySrv;
import com.rbkmoney.fraudbusters.repository.impl.FraudResultRepository;
import com.rbkmoney.fraudbusters.util.BeanUtil;
import com.rbkmoney.woody.thrift.impl.http.THClientBuilder;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.thrift.TException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import ru.yandex.clickhouse.ClickHouseDataSource;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.util.concurrent.ExecutionException;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;


@Slf4j
@RunWith(SpringRunner.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@SpringBootTest(webEnvironment = RANDOM_PORT, classes = FraudBustersApplication.class, properties = "kafka.listen.result.concurrency=1")
@ContextConfiguration(initializers = PreLoadTest.Initializer.class)
public class PreLoadTest extends KafkaAbstractTest {

    private static final String TEMPLATE = "rule: 12 >= 1\n" +
            " -> accept;";
    private static final String TEST = "test";

    private InspectorProxySrv.Iface client;

    @MockBean
    ClickHouseDataSource clickHouseDataSource;

    @MockBean
    JdbcTemplate jdbcTemplate;

    @MockBean
    FraudResultRepository paymentRepository;

    @LocalServerPort
    int serverPort;

    private static String SERVICE_URL = "http://localhost:%s/fraud_inspector/v1";

    public static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

        public static final String FULL_TEMPLATE = "full_template";

        @Override
        public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
            try {
                createTemplate();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }

        private static void createTemplate() throws InterruptedException, ExecutionException {

            try (Producer<String, Command> producer = createProducer()) {
                Command command = new Command();
                Template template = new Template();
                String id = TEST;
                template.setId(id);
                template.setTemplate(PreLoadTest.TEMPLATE.getBytes());
                command.setCommandBody(CommandBody.template(template));
                command.setCommandType(com.rbkmoney.damsel.fraudbusters.CommandType.CREATE);
                command.setCommandTime(LocalDateTime.now().toString());

                ProducerRecord<String, Command> producerRecord = new ProducerRecord<>(FULL_TEMPLATE,
                        id, command);
                producer.send(producerRecord).get();
            }
        }
    }

    @Before
    public void init() throws ExecutionException, InterruptedException {
        produceReferenceWithWait(true, null, null, TEST, 10);
    }

    @Test
    public void inspectPaymentTest() throws URISyntaxException, TException {
        waitingTopic(kafkaTopics.getTemplate());
        waitingTopic(kafkaTopics.getReference());

        THClientBuilder clientBuilder = new THClientBuilder()
                .withAddress(new URI(String.format(SERVICE_URL, serverPort)))
                .withNetworkTimeout(300000);
        client = clientBuilder.build(InspectorProxySrv.Iface.class);

        Context context = BeanUtil.createContext();
        RiskScore riskScore = client.inspectPayment(context);

        Assert.assertEquals(RiskScore.low, riskScore);
    }

}