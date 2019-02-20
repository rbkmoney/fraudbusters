package com.rbkmoney.fraudbusters;

import com.rbkmoney.damsel.geo_ip.GeoIpServiceSrv;
import com.rbkmoney.damsel.wb_list.WbListServiceSrv;
import com.rbkmoney.fraudbusters.constant.CommandType;
import com.rbkmoney.fraudbusters.constant.TemplateLevel;
import com.rbkmoney.fraudbusters.domain.FraudRequest;
import com.rbkmoney.fraudbusters.domain.RuleTemplate;
import com.rbkmoney.fraudbusters.serde.FraudRequestSerializer;
import com.rbkmoney.fraudbusters.serde.RuleTemplateSerializer;
import com.rbkmoney.fraudbusters.util.KeyGenerator;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.AfterClass;
import org.junit.ClassRule;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.testcontainers.containers.KafkaContainer;

import java.util.Properties;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@ContextConfiguration(classes = FraudBustersApplication.class, initializers = KafkaAbstractTest.Initializer.class)
public abstract class KafkaAbstractTest {

    @MockBean
    GeoIpServiceSrv.Iface geoIpServiceSrv;

    @MockBean
    WbListServiceSrv.Iface wbListServiceSrv;

    private static final String CONFLUENT_PLATFORM_VERSION = "5.0.1";

    @ClassRule
    public static KafkaContainer kafka = new KafkaContainer(CONFLUENT_PLATFORM_VERSION).withEmbeddedZookeeper();

    @Value("${kafka.template.topic}")
    public String templateTopic;

    @Autowired
    private ReplyingKafkaTemplate replyingKafkaTemplate;

    @Autowired
    private KafkaListenerEndpointRegistry registry;

    public static Producer<String, RuleTemplate> createProducer() {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafka.getBootstrapServers());
        props.put(ProducerConfig.CLIENT_ID_CONFIG, KeyGenerator.generateKey("client_id_"));
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, RuleTemplateSerializer.class.getName());
        return new KafkaProducer<>(props);
    }

    public static Producer<String, FraudRequest> createProducerGlobal() {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafka.getBootstrapServers());
        props.put(ProducerConfig.CLIENT_ID_CONFIG, KeyGenerator.generateKey("global"));
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, FraudRequestSerializer.class.getName());
        return new KafkaProducer<>(props);
    }

    public static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        @Override
        public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
            TestPropertyValues
                    .of("kafka.bootstrap.servers=" + kafka.getBootstrapServers())
                    .applyTo(configurableApplicationContext.getEnvironment());

            Producer<String, RuleTemplate> producer = createProducer();
            RuleTemplate ruleTemplate = new RuleTemplate();
            ruleTemplate.setLvl(TemplateLevel.GLOBAL);
            ruleTemplate.setCommandType(CommandType.DELETE);
            ruleTemplate.setTemplate("");
            ProducerRecord<String, RuleTemplate> producerRecord = new ProducerRecord<>("template",
                    TemplateLevel.GLOBAL.toString(), ruleTemplate);
            try {
                producer.send(producerRecord).get();
            } catch (Exception e) {
                log.error("KafkaAbstractTest initialize e: ", e);
            }
            producer.close();

            Producer<String, FraudRequest> producerNew = createProducerGlobal();
            FraudRequest fraudRequest = new FraudRequest();
            ProducerRecord<String, FraudRequest> producerRecordNew = new ProducerRecord<>("global_topic",
                    TemplateLevel.GLOBAL.toString(), fraudRequest);
            try {
                producerNew.send(producerRecordNew).get();
            } catch (Exception e) {
                log.error("Erro when initialize e: ", e);
            }
            producerNew.close();
        }
    }
}
