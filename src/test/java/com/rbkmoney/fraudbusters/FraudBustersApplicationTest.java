package com.rbkmoney.fraudbusters;

import com.rbkmoney.damsel.domain.RiskScore;
import com.rbkmoney.damsel.proxy_inspector.Context;
import com.rbkmoney.damsel.proxy_inspector.InspectorProxySrv;
import com.rbkmoney.fraudbusters.constant.Level;
import com.rbkmoney.fraudbusters.domain.RuleTemplate;
import com.rbkmoney.fraudbusters.serde.RuleTemplateSerializer;
import com.rbkmoney.fraudbusters.template.pool.StreamPool;
import com.rbkmoney.fraudbusters.util.BeanUtil;
import com.rbkmoney.woody.thrift.impl.http.THClientBuilder;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.thrift.TException;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.testcontainers.containers.KafkaContainer;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@ContextConfiguration(classes = FraudBustersApplication.class, initializers = FraudBustersApplicationTest.Initializer.class)
public class FraudBustersApplicationTest {

    public static final String CONCRETE = "concrete";
    public static final String TEMPLATE = "rule: 12 >= 1\n" +
            " -> accept;";
    public static final long SLEEP = 1000L;

    @ClassRule
    public static KafkaContainer kafka = new KafkaContainer("5.0.1").withEmbeddedZookeeper();

    @Autowired
    public StreamPool streamPool;
    @Value("${kafka.template.topic}")
    public String templateTopic;

    public static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        @Override
        public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
            TestPropertyValues
                    .of("kafka.bootstrap.servers=" + kafka.getBootstrapServers())
                    .applyTo(configurableApplicationContext.getEnvironment());
        }
    }

    public static Producer<String, RuleTemplate> createProducer() {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafka.getBootstrapServers());
        props.put(ProducerConfig.CLIENT_ID_CONFIG, "client_id_" + UUID.randomUUID().toString());
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, RuleTemplateSerializer.class.getName());
        return new KafkaProducer<>(props);
    }

    @Test
    public void testGlobal() throws ExecutionException, InterruptedException, TException {
        Producer<String, RuleTemplate> producer = createProducer();

        RuleTemplate ruleTemplate = new RuleTemplate();
        ruleTemplate.setLvl(Level.GLOBAL);
        ruleTemplate.setTemplate(TEMPLATE);
        ProducerRecord<String, RuleTemplate> producerRecord = new ProducerRecord<>(templateTopic,
                Level.GLOBAL.toString(), ruleTemplate);
        producer.send(producerRecord).get();
        Thread.sleep(SLEEP);
        KafkaStreams kafkaStreams = streamPool.get(Level.GLOBAL.toString());
        Assert.assertNotNull(kafkaStreams);
    }

    @Test
    public void testConcrete() throws ExecutionException, InterruptedException, TException {
        RuleTemplate ruleTemplate = new RuleTemplate();

        ruleTemplate.setLvl(Level.CONCRETE);
        ruleTemplate.setLocalId(CONCRETE);
        ruleTemplate.setTemplate(TEMPLATE);
        ProducerRecord<String, RuleTemplate> producerRecord = new ProducerRecord<>(templateTopic,
                CONCRETE, ruleTemplate);
        Producer<String, RuleTemplate> producer = createProducer();

        producer.send(producerRecord).get();

        Thread.sleep(SLEEP);
        KafkaStreams kafkaStreams = streamPool.get(CONCRETE);
        Assert.assertNotNull(kafkaStreams);
    }

    private InspectorProxySrv.Iface client;

    @LocalServerPort
    int serverPort;

    private static String SERVICE_URL = "http://localhost:%s/v1/fraud_inspector";

    @Test
    public void test() throws URISyntaxException, TException, ExecutionException, InterruptedException {
        THClientBuilder clientBuilder = new THClientBuilder()
                .withAddress(new URI(String.format(SERVICE_URL, serverPort)))
                .withNetworkTimeout(30000);
        client = clientBuilder.build(InspectorProxySrv.Iface.class);

        Producer<String, RuleTemplate> producer = createProducer();

        RuleTemplate ruleTemplate = new RuleTemplate();
        ruleTemplate.setLvl(Level.GLOBAL);
        ruleTemplate.setTemplate(TEMPLATE);
        ProducerRecord<String, RuleTemplate> producerRecord = new ProducerRecord<>(templateTopic,
                Level.GLOBAL.toString(), ruleTemplate);
        producer.send(producerRecord).get();

        Context context = BeanUtil.createContext();
        RiskScore riskScore = client.inspectPayment(context);

        Assert.assertEquals(riskScore, RiskScore.low);
    }
}