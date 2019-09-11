package com.rbkmoney.fraudbusters.listener;

import com.rbkmoney.damsel.fraudbusters.Command;
import com.rbkmoney.fraudbusters.stream.TemplateStreamFactoryImpl;
import com.rbkmoney.kafka.common.loader.PreloadListener;
import com.rbkmoney.kafka.common.loader.PreloadListenerImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.streams.KafkaStreams;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Properties;

@Slf4j
@Component
@RequiredArgsConstructor
public class StartupListener implements ApplicationListener<ContextRefreshedEvent> {

    public static final long PRELOAD_TIMEOUT = 30000L;
    private final TemplateStreamFactoryImpl templateStreamFactoryImpl;
    private final Properties fraudStreamProperties;
    private final ConsumerFactory<String, Command> templateListenerFactory;
    private final ConsumerFactory<String, Command> groupListenerFactory;
    private final ConsumerFactory<String, Command> referenceListenerFactory;
    private final ConsumerFactory<String, Command> groupReferenceListenerFactory;
    private final TemplateListener templateListener;
    private final GroupListener groupListener;
    private final GroupReferenceListener groupReferenceListener;
    private final TemplateReferenceListener templateReferenceListener;

    private KafkaStreams kafkaStreams;
    private PreloadListener<String, Command> preloadListener = new PreloadListenerImpl<>();

    @Value("${kafka.topic.template}")
    private String topic;

    @Value("${kafka.topic.reference}")
    private String topicReference;

    @Value("${kafka.topic.group.list}")
    private String topicGroup;

    @Value("${kafka.topic.group.reference}")
    private String topicGroupReference;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        try {
            long startPreloadTime = System.currentTimeMillis();
            Thread threadTemplate = new Thread(() -> waitPreLoad(templateListenerFactory, topic, templateListener));
            threadTemplate.start();
            Thread threadReference = new Thread(() -> waitPreLoad(referenceListenerFactory, topicReference, templateReferenceListener));
            threadReference.start();
            Thread threadGroup = new Thread(() -> waitPreLoad(groupListenerFactory, topicGroup, groupListener));
            threadGroup.start();
            Thread threadGroupReference = new Thread(() -> waitPreLoad(groupReferenceListenerFactory, topicGroupReference, groupReferenceListener));
            threadGroupReference.start();
            threadTemplate.join(PRELOAD_TIMEOUT);
            threadReference.join(PRELOAD_TIMEOUT);
            threadGroup.join(PRELOAD_TIMEOUT);
            threadGroupReference.join(PRELOAD_TIMEOUT);
            kafkaStreams = templateStreamFactoryImpl.create(fraudStreamProperties);
            log.info("StartupListener start stream preloadTime: {} ms kafkaStreams: {}", System.currentTimeMillis() - startPreloadTime,
                    kafkaStreams.allMetadata());
        } catch (InterruptedException e) {
            log.error("StartupListener onApplicationEvent e: ", e);
            Thread.currentThread().interrupt();
        }
    }

    public void stop() {
        kafkaStreams.close(Duration.ofSeconds(10L));
    }

    private void waitPreLoad(ConsumerFactory<String, Command> groupListenerFactory, String topic, CommandListener listener) {
        Consumer<String, Command> consumer = groupListenerFactory.createConsumer();
        preloadListener.preloadToLastOffsetInPartition(consumer, topic, 0, listener::listen);
        consumer.close();
    }
}