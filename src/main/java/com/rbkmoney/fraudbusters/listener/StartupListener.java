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
    private final ConsumerFactory<String, Command> referenceListenerFactory;
    private final TemplateListener templateListener;
    private final TemplateReferenceListener templateReferenceListener;

    private KafkaStreams kafkaStreams;
    private PreloadListener<String, Command> preloadListener = new PreloadListenerImpl<>();

    @Value("${kafka.topic.template}")
    private String topic;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        try {
            Thread threadTemplate = new Thread(this::waitForLoadTemplates);
            threadTemplate.start();
            Thread threadReference = new Thread(this::waitForLoadReferences);
            threadReference.start();
            threadTemplate.join(PRELOAD_TIMEOUT);
            threadReference.join(PRELOAD_TIMEOUT);
            kafkaStreams = templateStreamFactoryImpl.create(fraudStreamProperties);
            log.info("StartupListener start stream kafkaStreams: ", kafkaStreams.allMetadata());
        } catch (InterruptedException e) {
            log.error("StartupListener onApplicationEvent e: ", e);
            Thread.currentThread().interrupt();
        }
    }

    public void stop() {
        kafkaStreams.close(Duration.ofSeconds(10L));
    }

    private void waitForLoadTemplates() {
        Consumer<String, Command> consumer = templateListenerFactory.createConsumer();
        preloadListener.preloadToLastOffsetInPartition(consumer, topic, 0, templateListener::listen);
        consumer.close();
    }

    private void waitForLoadReferences() {
        Consumer<String, Command> consumer = referenceListenerFactory.createConsumer();
        preloadListener.preloadToLastOffsetInPartition(consumer, topic, 0, templateReferenceListener::listen);
        consumer.close();
    }
}