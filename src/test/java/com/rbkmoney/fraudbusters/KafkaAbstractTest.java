package com.rbkmoney.fraudbusters;

import com.rbkmoney.damsel.fraudbusters.*;
import com.rbkmoney.damsel.geo_ip.GeoIpServiceSrv;
import com.rbkmoney.damsel.wb_list.WbListServiceSrv;
import com.rbkmoney.fraudbusters.config.properties.KafkaTopics;
import com.rbkmoney.fraudbusters.serde.CommandDeserializer;
import com.rbkmoney.fraudbusters.service.FraudManagementService;
import com.rbkmoney.fraudbusters.util.BeanUtil;
import com.rbkmoney.fraudbusters.util.KeyGenerator;
import com.rbkmoney.fraudbusters.util.ReferenceKeyGenerator;
import com.rbkmoney.kafka.common.serialization.ThriftSerializer;
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
import org.mockito.Mockito;
import org.rnorth.ducttape.unreliables.Unreliables;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import static org.mockito.ArgumentMatchers.any;

@Slf4j
@ContextConfiguration(classes = KafkaTopics.class)
public abstract class KafkaAbstractTest {

    protected static final long TIMEOUT = 1000L;

    @MockBean
    private FraudManagementService fraudManagementService;

    @MockBean
    GeoIpServiceSrv.Iface geoIpServiceSrv;

    @MockBean
    WbListServiceSrv.Iface wbListServiceSrv;

    public static final String CONFLUENT_PLATFORM_VERSION = "5.0.1";

    @Autowired
    protected KafkaTopics kafkaTopics;

    @Value("${kafka.topic.event.sink.initial}")
    public String eventSinkTopic;
    @Value("${kafka.topic.event.sink.aggregated}")
    public String aggregatedEventSink;
    @Value("${kafka.topic.fraud.payment}")
    public String fraudPaymentTopic;

    @Before
    public void setUp() {
        Mockito.when(fraudManagementService.isNewShop(any())).thenReturn(false);
    }

    public Producer<String, Command> createProducer() {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, getBootstrapServers());
        props.put(ProducerConfig.CLIENT_ID_CONFIG, KeyGenerator.generateKey("client_id_"));
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, ThriftSerializer.class.getName());
        props.put(ProducerConfig.RETRIES_CONFIG, 3);
        props.put(ProducerConfig.RETRY_BACKOFF_MS_CONFIG, 1000);
        return new KafkaProducer<>(props);
    }

    <T> Consumer<String, T> createConsumer(Class clazz) {
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, getBootstrapServers());
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, clazz);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, UUID.randomUUID().toString());
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
            command.setCommandTime(LocalDateTime.now().toString());
            String key = ReferenceKeyGenerator.generateTemplateKey(value);
            ProducerRecord<String, Command> producerRecord = new ProducerRecord<>(kafkaTopics.getFullReference(), key, command);
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
            command.setCommandTime(LocalDateTime.now().toString());

            String key = ReferenceKeyGenerator.generateP2PTemplateKey(value);

            ProducerRecord<String, Command> producerRecord = new ProducerRecord<>(kafkaTopics.getP2pReference(), key, command);
            producer.send(producerRecord).get();
        }
    }

    void produceReferenceWithWait(boolean isGlobal, String party, String shopId, String idTemplate, int timeout) throws InterruptedException, ExecutionException {
        produceReference(isGlobal, party, shopId, idTemplate);
        try (Consumer<String, Object> consumer = createConsumer(CommandDeserializer.class)) {
            consumer.subscribe(List.of(kafkaTopics.getFullReference()));
            Unreliables.retryUntilTrue(timeout, TimeUnit.SECONDS, () -> {
                ConsumerRecords<String, Object> records = consumer.poll(Duration.ofSeconds(1L));
                return !records.isEmpty();
            });
        }
    }

    void produceGroupReference(String party, String shopId, String idGroup) throws InterruptedException, ExecutionException {
        try (Producer<String, Command> producer = createProducer()) {
            Command command = BeanUtil.createGroupReferenceCommand(party, shopId, idGroup);
            String key = ReferenceKeyGenerator.generateTemplateKeyByList(party, shopId);
            ProducerRecord<String, Command> producerRecord = new ProducerRecord<>(kafkaTopics.getFullGroupReference(), key, command);
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
        command.setCommandTime(LocalDateTime.now().toString());
        return command;
    }

    protected void waitingTopic(String topicName) {
        try (Consumer<String, Object> consumer = createConsumer(CommandDeserializer.class)) {
            consumer.subscribe(List.of(topicName));
            Unreliables.retryUntilTrue(240, TimeUnit.SECONDS, () -> {
                ConsumerRecords<String, Object> records = consumer.poll(Duration.ofSeconds(1L));
                return !records.isEmpty();
            });
        }
    }

    protected abstract String getBootstrapServers();
}
