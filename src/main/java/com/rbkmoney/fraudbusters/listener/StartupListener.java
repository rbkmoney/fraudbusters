package com.rbkmoney.fraudbusters.listener;

import com.rbkmoney.damsel.fraudbusters.Command;
import com.rbkmoney.fraudbusters.exception.StartException;
import com.rbkmoney.fraudbusters.listener.payment.GroupListener;
import com.rbkmoney.fraudbusters.listener.payment.GroupReferenceListener;
import com.rbkmoney.fraudbusters.listener.payment.TemplateListener;
import com.rbkmoney.fraudbusters.listener.payment.TemplateReferenceListener;
import com.rbkmoney.fraudbusters.stream.TemplateStreamFactory;
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
import java.util.List;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class StartupListener implements ApplicationListener<ContextRefreshedEvent> {

    private static final long PRELOAD_TIMEOUT = 30L;
    private static final int COUNT_PRELOAD_TASKS = 4;
    public static final long CLOSE_STREAM_TIMEOUT_SECONDS = 10L;

    private final TemplateStreamFactory eventSinkAggregationStreamFactoryImpl;
    private final Properties eventSinkStreamProperties;
    private final ConsumerFactory<String, Command> templateListenerFactory;
    private final ConsumerFactory<String, Command> groupListenerFactory;
    private final ConsumerFactory<String, Command> referenceListenerFactory;
    private final ConsumerFactory<String, Command> groupReferenceListenerFactory;
    private final TemplateListener templateListener;
    private final GroupListener groupListener;
    private final GroupReferenceListener groupReferenceListener;
    private final TemplateReferenceListener templateReferenceListener;

    private KafkaStreams eventSinkStream;

    private PreloadListener<String, Command> preloadListener = new PreloadListenerImpl<>();

    @Value("${kafka.topic.template}")
    private String topicTemplate;

    @Value("${kafka.topic.reference}")
    private String topicReference;

    @Value("${kafka.topic.group.list}")
    private String topicGroup;

    @Value("${kafka.topic.group.reference}")
    private String topicGroupReference;

    @Value("${kafka.stream.event.sink.enable}")
    private boolean enableEventSinkStream;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        try {
            long startPreloadTime = System.currentTimeMillis();

            ExecutorService executorService = Executors.newFixedThreadPool(COUNT_PRELOAD_TASKS);
            CountDownLatch latch = new CountDownLatch(COUNT_PRELOAD_TASKS);
            List<Runnable> tasks = List.of(
                    () -> waitPreLoad(latch, templateListenerFactory, topicTemplate, templateListener),
                    () -> waitPreLoad(latch, referenceListenerFactory, topicReference, templateReferenceListener),
                    () -> waitPreLoad(latch, groupListenerFactory, topicGroup, groupListener),
                    () -> waitPreLoad(latch, groupReferenceListenerFactory, topicGroupReference, groupReferenceListener)
            );
            tasks.forEach(executorService::submit);
            long timeout = PRELOAD_TIMEOUT * COUNT_PRELOAD_TASKS;
            boolean await = latch.await(timeout, TimeUnit.SECONDS);

            if (!await) {
                throw new StartException("Cant load all rules by timeout: " + timeout);
            }

            if (enableEventSinkStream) {
                eventSinkStream = eventSinkAggregationStreamFactoryImpl.create(eventSinkStreamProperties);
                log.info("StartupListener start stream preloadTime: {} ms eventSinkStream: {}", System.currentTimeMillis() - startPreloadTime,
                        eventSinkStream.allMetadata());
            }
        } catch (InterruptedException e) {
            log.error("StartupListener onApplicationEvent e: ", e);
            Thread.currentThread().interrupt();
        }
    }

    public void stop() {
        if (eventSinkStream != null) {
            eventSinkStream.close(Duration.ofSeconds(CLOSE_STREAM_TIMEOUT_SECONDS));
        }
    }

    private void waitPreLoad(CountDownLatch latch, ConsumerFactory<String, Command> groupListenerFactory, String topic, CommandListener listener) {
        Consumer<String, Command> consumer = groupListenerFactory.createConsumer();
        preloadListener.preloadToLastOffsetInPartition(consumer, topic, 0, listener::listen);
        consumer.close();
        latch.countDown();
    }

}