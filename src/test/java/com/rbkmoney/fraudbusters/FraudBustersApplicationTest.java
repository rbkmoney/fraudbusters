package com.rbkmoney.fraudbusters;

import com.rbkmoney.fraudbusters.constant.Level;
import com.rbkmoney.fraudbusters.domain.RuleTemplate;
import com.rbkmoney.fraudbusters.serde.RuleTemplateSerializer;
import com.rbkmoney.fraudbusters.template.pool.StreamPool;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.kafka.streams.KafkaStreams;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.testcontainers.containers.KafkaContainer;

import java.util.Properties;
import java.util.concurrent.ExecutionException;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@ContextConfiguration(classes = FraudBustersApplication.class, initializers = FraudBustersApplicationTest.Initializer.class)
public class FraudBustersApplicationTest {

    public static final String CONCRETE = "concrete";
    public static final String TEMPLATE = "rule: 12 >= 1\n" +
            " -> 3ds;";
    public static final long SLEEP = 1000L;
    @ClassRule
    public static KafkaContainer kafka = new KafkaContainer("5.0.1");

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
        props.put(ProducerConfig.CLIENT_ID_CONFIG, "client_id");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, RuleTemplateSerializer.class.getName());
        return new KafkaProducer<>(props);
    }

    @Test
    public void testRawPayoutRepository() throws ExecutionException, InterruptedException {
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

        ruleTemplate.setLvl(Level.CONCRETE);
        ruleTemplate.setLocalId(CONCRETE);
        ruleTemplate.setTemplate(TEMPLATE);
        producerRecord = new ProducerRecord<>(templateTopic,
                CONCRETE, ruleTemplate);
        producer.send(producerRecord).get();

        Thread.sleep(SLEEP);
        kafkaStreams = streamPool.get(CONCRETE);
        Assert.assertNotNull(kafkaStreams);
    }
}