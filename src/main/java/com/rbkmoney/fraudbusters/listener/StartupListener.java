package com.rbkmoney.fraudbusters.listener;

import com.rbkmoney.damsel.fraudbusters.Command;
import com.rbkmoney.fraudbusters.config.properties.KafkaTopics;
import com.rbkmoney.fraudbusters.exception.StartException;
import com.rbkmoney.fraudbusters.listener.p2p.GroupP2PListener;
import com.rbkmoney.fraudbusters.listener.p2p.GroupReferenceP2PListener;
import com.rbkmoney.fraudbusters.listener.p2p.TemplateP2PListener;
import com.rbkmoney.fraudbusters.listener.p2p.TemplateP2PReferenceListener;
import com.rbkmoney.fraudbusters.listener.payment.GroupListener;
import com.rbkmoney.fraudbusters.listener.payment.GroupReferenceListener;
import com.rbkmoney.fraudbusters.listener.payment.TemplateListener;
import com.rbkmoney.fraudbusters.listener.payment.TemplateReferenceListener;
import com.rbkmoney.fraudbusters.pool.Pool;
import com.rbkmoney.fraudbusters.service.PoolMonitoringService;
import com.rbkmoney.fraudbusters.stream.StreamManager;
import com.rbkmoney.kafka.common.loader.PreloadListener;
import com.rbkmoney.kafka.common.loader.PreloadListenerImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.antlr.v4.runtime.ParserRuleContext;
import org.apache.kafka.clients.consumer.Consumer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class StartupListener implements ApplicationListener<ContextRefreshedEvent> {

    private static final int COUNT_PRELOAD_TASKS = 8;

    @Value("${preload.timeout:20}")
    private long preloadTimeout;

    @Value("${kafka.historical.listener.enable}")
    private boolean historicalListenerEnabled;

    private final StreamManager streamManager;

    private final ConsumerFactory<String, Command> templateListenerFactory;
    private final ConsumerFactory<String, Command> groupListenerFactory;
    private final ConsumerFactory<String, Command> referenceListenerFactory;
    private final ConsumerFactory<String, Command> groupReferenceListenerFactory;

    private final TemplateListener templateListener;
    private final GroupListener groupListener;
    private final GroupReferenceListener groupReferenceListener;
    private final TemplateReferenceListener templateReferenceListener;

    private final ConsumerFactory<String, Command> timeTemplateListenerFactory;
    private final ConsumerFactory<String, Command> timeGroupListenerFactory;
    private final ConsumerFactory<String, Command> timeReferenceListenerFactory;
    private final ConsumerFactory<String, Command> timeGroupReferenceListenerFactory;

    private final TemplateListener timeTemplateListener;
    private final GroupListener timeGroupListener;
    private final GroupReferenceListener timeGroupReferenceListener;
    private final TemplateReferenceListener timeTemplateReferenceListener;

    private final ConsumerFactory<String, Command> templateP2PListenerFactory;
    private final ConsumerFactory<String, Command> groupP2PListenerFactory;
    private final ConsumerFactory<String, Command> referenceP2PListenerFactory;
    private final ConsumerFactory<String, Command> groupReferenceP2PListenerFactory;

    private final TemplateP2PListener templateP2PListener;
    private final GroupP2PListener groupP2PListener;
    private final GroupReferenceP2PListener groupReferenceP2PListener;
    private final TemplateP2PReferenceListener templateP2PReferenceListener;

    private final Pool<ParserRuleContext> templatePoolImpl;
    private final Pool<ParserRuleContext> templateP2PPoolImpl;

    private final PreloadListener<String, Command> preloadListener = new PreloadListenerImpl<>();
    private final KafkaTopics kafkaTopics;

    private final PoolMonitoringService poolMonitoringService;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        try {
            long startPreloadTime = System.currentTimeMillis();

            poolMonitoringService.addPoolsToMonitoring();

            List<Runnable> tasks = new ArrayList<>();
            CountDownLatch latch;
            if (historicalListenerEnabled) {
                latch = new CountDownLatch(COUNT_PRELOAD_TASKS + 4);
                initRewriteStream();
                tasks.addAll(List.of(
                        () -> waitPreLoad(latch, timeTemplateListenerFactory, kafkaTopics.getFullTemplate(), timeTemplateListener),
                        () -> waitPreLoad(latch, timeReferenceListenerFactory, kafkaTopics.getFullReference(), timeTemplateReferenceListener),
                        () -> waitPreLoad(latch, timeGroupListenerFactory, kafkaTopics.getFullGroupList(), timeGroupListener),
                        () -> waitPreLoad(latch, timeGroupReferenceListenerFactory, kafkaTopics.getFullGroupReference(), timeGroupReferenceListener)));
            } else {
                latch = new CountDownLatch(COUNT_PRELOAD_TASKS);
            }

            ExecutorService executorService = Executors.newFixedThreadPool(COUNT_PRELOAD_TASKS);

            tasks.addAll(List.of(
                    () -> waitPreLoad(latch, templateListenerFactory, kafkaTopics.getTemplate(), templateListener),
                    () -> waitPreLoad(latch, referenceListenerFactory, kafkaTopics.getReference(), templateReferenceListener),
                    () -> waitPreLoad(latch, groupListenerFactory, kafkaTopics.getGroupList(), groupListener),
                    () -> waitPreLoad(latch, groupReferenceListenerFactory, kafkaTopics.getGroupReference(), groupReferenceListener),

                    () -> waitPreLoad(latch, templateP2PListenerFactory, kafkaTopics.getP2pTemplate(), templateP2PListener),
                    () -> waitPreLoad(latch, referenceP2PListenerFactory, kafkaTopics.getP2pReference(), templateP2PReferenceListener),
                    () -> waitPreLoad(latch, groupP2PListenerFactory, kafkaTopics.getP2pGroupList(), groupP2PListener),
                    () -> waitPreLoad(latch, groupReferenceP2PListenerFactory, kafkaTopics.getP2pGroupReference(), groupReferenceP2PListener))
            );

            tasks.forEach(executorService::submit);
            long timeout = preloadTimeout * COUNT_PRELOAD_TASKS;
            boolean await = latch.await(timeout, TimeUnit.SECONDS);

            if (!await) {
                throw new StartException("Cant load all rules by timeout: " + timeout);
            }

            log.info("StartupListener start stream preloadTime: {} ms", System.currentTimeMillis() - startPreloadTime);
            log.info("StartupListener load pool payment template size: {} templates: {}", templatePoolImpl.size(), templatePoolImpl);
            log.info("StartupListener load pool p2p template size: {} templates: {}", templateP2PPoolImpl.size(), templateP2PPoolImpl);
        } catch (InterruptedException e) {
            log.error("StartupListener onApplicationEvent e: ", e);
            Thread.currentThread().interrupt();
        }
    }

    private void initRewriteStream() {
        streamManager.createStream(kafkaTopics.getFullTemplate(), kafkaTopics.getTemplate(), "template-stream");
        streamManager.createStream(kafkaTopics.getFullReference(), kafkaTopics.getReference(), "reference-stream");
        streamManager.createStream(kafkaTopics.getFullGroupList(), kafkaTopics.getGroupList(), "group-stream");
        streamManager.createStream(kafkaTopics.getFullGroupReference(), kafkaTopics.getGroupReference(), "group-ref-stream");
    }

    private void waitPreLoad(CountDownLatch latch, ConsumerFactory<String, Command> groupListenerFactory, String topic, CommandListener listener) {
        try (Consumer<String, Command> consumer = groupListenerFactory.createConsumer()) {
            preloadListener.preloadToLastOffsetInPartition(consumer, topic, 0, listener::listen);
        }
        latch.countDown();
    }

}