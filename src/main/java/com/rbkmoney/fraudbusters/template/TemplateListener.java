package com.rbkmoney.fraudbusters.template;

import com.rbkmoney.fraudbusters.domain.RuleTemplate;
import com.rbkmoney.fraudbusters.serde.RuleTemplateDeserializer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@RequiredArgsConstructor
public class TemplateListener implements Runnable {

    private static final String GROUP_ID = "TemplateListener-";
    private AtomicBoolean stopped = new AtomicBoolean(false);

    private final String listenTopic;
    private final String bootstrapServers;
    private final TemplateBroker templateBroker;

    @Override
    public void run() {
        final Consumer<String, RuleTemplate> consumer = create(listenTopic);
        while (!stopped.get() && !Thread.currentThread().isInterrupted()) {
            final ConsumerRecords<String, RuleTemplate> consumerRecords = consumer.poll(Duration.ofMillis(100));
            consumerRecords.forEach(record -> {
                templateBroker.doDispatch(record.value());
                log.info("apply template: {}", record);
            });
        }
        consumer.close();
    }


    public void stop() {
        stopped.set(true);
    }

    private Consumer<String, RuleTemplate> create(String topic) {
        final Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, GROUP_ID + UUID.randomUUID().toString());
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, RuleTemplateDeserializer.class.getName());
        final Consumer<String, RuleTemplate> consumer = new KafkaConsumer<>(props);
        consumer.subscribe(Collections.singletonList(topic));
        return consumer;
    }
}
