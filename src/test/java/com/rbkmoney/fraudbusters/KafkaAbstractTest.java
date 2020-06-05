package com.rbkmoney.fraudbusters;

import com.rbkmoney.damsel.fraudbusters.*;
import com.rbkmoney.damsel.geo_ip.GeoIpServiceSrv;
import com.rbkmoney.damsel.wb_list.WbListServiceSrv;
import com.rbkmoney.fraudbusters.listener.p2p.TemplateP2PReferenceListener;
import com.rbkmoney.fraudbusters.serde.CommandDeserializer;
import com.rbkmoney.fraudbusters.service.FraudManagementService;
import com.rbkmoney.fraudbusters.util.BeanUtil;
import com.rbkmoney.fraudbusters.util.KeyGenerator;
import com.rbkmoney.fraudbusters.util.ReferenceKeyGenerator;
import com.rbkmoney.kafka.common.serialization.ThriftSerializer;
import com.rbkmoney.machinegun.eventsink.MachineEvent;
import com.rbkmoney.machinegun.eventsink.SinkEvent;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.jetbrains.annotations.NotNull;
import org.junit.Before;
import org.junit.ClassRule;
import org.mockito.Mockito;
import org.rnorth.ducttape.unreliables.Unreliables;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.testcontainers.containers.KafkaContainer;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import static org.mockito.ArgumentMatchers.any;

@Slf4j
@ContextConfiguration(initializers = KafkaAbstractTest.Initializer.class)
public abstract class KafkaAbstractTest {

    @MockBean
    private FraudManagementService fraudManagementService;

    @MockBean
    GeoIpServiceSrv.Iface geoIpServiceSrv;

    @MockBean
    WbListServiceSrv.Iface wbListServiceSrv;

    public static final String CONFLUENT_PLATFORM_VERSION = "5.0.1";

    @ClassRule
    public static KafkaContainer kafka = new KafkaContainer(CONFLUENT_PLATFORM_VERSION).withEmbeddedZookeeper();

    @Value("${kafka.topic.template}")
    public String templateTopic;
    @Value("${kafka.topic.p2p.template}")
    public String templateTopicP2P;
    @Value("${kafka.topic.reference}")
    public String referenceTopic;
    @Value("${kafka.topic.p2p.reference}")
    public String referenceTopicP2P;
    @Value("${kafka.topic.group.list}")
    public String groupTopic;
    @Value("${kafka.topic.p2p.group.list}")
    public String groupTopicP2P;
    @Value("${kafka.topic.group.reference}")
    public String groupReferenceTopic;
    @Value("${kafka.topic.p2p.group.reference}")
    public String groupReferenceTopicP2P;
    @Value("${kafka.topic.event.sink.initial}")
    public String eventSinkTopic;
    @Value("${kafka.topic.event.sink.aggregated}")
    public String aggregatedEventSink;

    @Before
    public void setUp(){
        Mockito.when(fraudManagementService.isNewShop(any(), any())).thenReturn(false);
    }

    public static Producer<String, Command> createProducer() {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafka.getBootstrapServers());
        props.put(ProducerConfig.CLIENT_ID_CONFIG, KeyGenerator.generateKey("client_id_"));
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, ThriftSerializer.class.getName());
        return new KafkaProducer<>(props);
    }

    public static Producer<String, SinkEvent> createProducerAggr() {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafka.getBootstrapServers());
        props.put(ProducerConfig.CLIENT_ID_CONFIG, KeyGenerator.generateKey("aggr"));
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, ThriftSerializer.class.getName());
        return new KafkaProducer<>(props);
    }

    public static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        @Override
        public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
            TestPropertyValues
                    .of("kafka.bootstrap.servers=" + kafka.getBootstrapServers())
                    .applyTo(configurableApplicationContext.getEnvironment());
            initTopic("template");
            initTopic("template_p2p");
            initTopic("template_reference");
            initTopic("template_p2p_reference");
            initTopic("group_list");
            initTopic("group_p2p_list");
            initTopic("group_reference");
            initTopic("group_p2p_reference");
            initTopic("event_sink");
            initTopic("aggregated_event_sink");
        }

        @NotNull
        private <T> Consumer<String, T> initTopic(String topicName) {
            Consumer<String, T> consumer = createConsumer(CommandDeserializer.class);
            try {
                consumer.subscribe(Collections.singletonList(topicName));
                consumer.poll(Duration.ofMillis(100L));
            } catch (Exception e) {
                log.error("KafkaAbstractTest initialize e: ", e);
            }
            consumer.close();
            return consumer;
        }
    }

    static <T> Consumer<String, T> createConsumer(Class clazz) {
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafka.getBootstrapServers());
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, clazz);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "test");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        return new KafkaConsumer<>(props);
    }

    void produceTemplate(String localId, String templateString, String topicName) throws InterruptedException, ExecutionException {
        try (Producer<String, Command> producer = createProducer()) {
            Command command = crateCommandTemplate(localId, templateString);
            ProducerRecord<String, Command> producerRecord = new ProducerRecord<>(topicName, localId, command);
            producer.send(producerRecord).get();
        }
    }

    void produceGroup(String localId, List<PriorityId> priorityIds, String topic) throws InterruptedException, ExecutionException {
        try (Producer<String, Command> producer = createProducer()) {
            Command command = BeanUtil.createGroupCommand(localId, priorityIds);
            ProducerRecord<String, Command> producerRecord = new ProducerRecord<>(topic, localId, command);
            producer.send(producerRecord).get();
        }
    }

    void produceReference(boolean isGlobal, String party, String shopId, String idTemplate) throws InterruptedException, ExecutionException {
        try (Producer<String, Command> producer = createProducer()) {
            Command command = new Command();
            command.setCommandType(CommandType.CREATE);
            TemplateReference value = new TemplateReference();
            value.setTemplateId(idTemplate);
            value.setPartyId(party);
            value.setShopId(shopId);
            value.setIsGlobal(isGlobal);
            command.setCommandBody(CommandBody.reference(value));
            String key = ReferenceKeyGenerator.generateTemplateKey(value);
            ProducerRecord<String, Command> producerRecord = new ProducerRecord<>(referenceTopic, key, command);
            producer.send(producerRecord).get();
        }
    }

    void produceP2PReference(boolean isGlobal, String identityId, String idTemplate) throws InterruptedException, ExecutionException {
        try (Producer<String, Command> producer = createProducer()) {
            Command command = new Command();
            command.setCommandType(CommandType.CREATE);
            P2PReference value = new P2PReference();
            value.setTemplateId(idTemplate);
            value.setIdentityId(identityId);
            value.setIsGlobal(isGlobal);

            command.setCommandBody(CommandBody.p2p_reference(value));
            String key = ReferenceKeyGenerator.generateP2PTemplateKey(value);

            ProducerRecord<String, Command> producerRecord = new ProducerRecord<>(referenceTopicP2P, key, command);
            producer.send(producerRecord).get();
        }
    }

    void produceReferenceWithWait(boolean isGlobal, String party, String shopId, String idTemplate, int timeout) throws InterruptedException, ExecutionException {
        produceReference(isGlobal, party, shopId, idTemplate);
        try (Consumer<String, Object> consumer = createConsumer(CommandDeserializer.class)) {
            consumer.subscribe(List.of(referenceTopic));
            Unreliables.retryUntilTrue(timeout, TimeUnit.SECONDS, () -> {
                ConsumerRecords<String, Object> records = consumer.poll(Duration.ofSeconds(1L));
                return !records.isEmpty();
            });
        }
    }

    void produceReferenceWithWait(boolean isGlobal, String identityId, String idTemplate, int timeout) throws InterruptedException, ExecutionException {
        produceP2PReference(isGlobal, identityId, idTemplate);
        try (Consumer<String, Object> consumer = createConsumer(CommandDeserializer.class)) {
            consumer.subscribe(List.of(referenceTopic));
            Unreliables.retryUntilTrue(timeout, TimeUnit.SECONDS, () -> {
                ConsumerRecords<String, Object> records = consumer.poll(Duration.ofSeconds(1L));
                return !records.isEmpty();
            });
        }
    }

    void produceGroupReference(String party, String shopId, String idGroup) throws InterruptedException, ExecutionException {
        try (Producer<String, Command> producer = createProducer()) {
            Command command = BeanUtil.createGroupReferenceCommand(party, shopId, idGroup);
            String key = ReferenceKeyGenerator.generateTemplateKey(party, shopId);
            ProducerRecord<String, Command> producerRecord = new ProducerRecord<>(groupReferenceTopic, key, command);
            producer.send(producerRecord).get();
        }
    }

    void produceGroupReference(String identityId, String idGroup) throws InterruptedException, ExecutionException {
        try (Producer<String, Command> producer = createProducer()) {
            Command command = BeanUtil.createP2PGroupReferenceCommand(identityId, idGroup);
            String key = ReferenceKeyGenerator.generateTemplateKeyByList(identityId);
            ProducerRecord<String, Command> producerRecord = new ProducerRecord<>(groupReferenceTopicP2P, key, command);
            producer.send(producerRecord).get();
        }
    }

    @NotNull
    private Command crateCommandTemplate(String localId, String templateString) {
        Command command = new Command();
        Template template = new Template();
        template.setId(localId);
        template.setTemplate(templateString.getBytes());
        command.setCommandBody(CommandBody.template(template));
        command.setCommandType(com.rbkmoney.damsel.fraudbusters.CommandType.CREATE);
        return command;
    }

    void produceMessageToEventSink(MachineEvent machineEvent) throws InterruptedException, ExecutionException {
        try (Producer<String, SinkEvent> producer = createProducerAggr()) {
            SinkEvent sinkEvent = new SinkEvent();
            sinkEvent.setEvent(machineEvent);
            ProducerRecord<String, SinkEvent> producerRecord = new ProducerRecord<>(eventSinkTopic,
                    sinkEvent.getEvent().getSourceId(), sinkEvent);
            producer.send(producerRecord).get();
        }
    }
}
