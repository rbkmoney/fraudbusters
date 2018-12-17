package com.rbkmoney.fraudbusters.template;

import com.rbkmoney.fraudbusters.domain.RuleTemplate;
import com.rbkmoney.fraudbusters.factory.TemplateListenerFactory;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.MockConsumer;
import org.apache.kafka.clients.consumer.OffsetResetStrategy;
import org.apache.kafka.common.TopicPartition;
import org.jetbrains.annotations.NotNull;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.HashMap;

import static org.mockito.ArgumentMatchers.any;

public class TemplateListenerTest {

    private static final String MY_TOPIC = "test";
    public static final long SLEEP = 100L;

    @Mock
    private TemplateDispatcherImpl templateDispatcherImpl;
    @Mock
    private TemplateListenerFactory templateListenerFactory;


    TemplateListener templateListener;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
        templateListener = new TemplateListener(templateDispatcherImpl, templateListenerFactory);
    }

    @NotNull
    private MockConsumer<String, RuleTemplate> initMockConsumer() {
        MockConsumer<String, RuleTemplate> mockConsumer = new MockConsumer<>(OffsetResetStrategy.EARLIEST);

        mockConsumer.assign(Collections.singletonList(new TopicPartition(MY_TOPIC, 0)));

        HashMap<TopicPartition, Long> beginningOffsets = new HashMap<>();
        beginningOffsets.put(new TopicPartition(MY_TOPIC, 0), 0L);
        mockConsumer.updateBeginningOffsets(beginningOffsets);
        HashMap<TopicPartition, Long> endOffsets = new HashMap<>();
        endOffsets.put(new TopicPartition(MY_TOPIC, 0), 3L);
        mockConsumer.addEndOffsets(endOffsets);

        return mockConsumer;
    }

    @Test
    public void run() throws InterruptedException {
        MockConsumer<String, RuleTemplate> mockConsumer = initMockConsumer();
        Mockito.when(templateListenerFactory.create()).thenReturn(mockConsumer);
        new Thread(() -> templateListener.run()).start();
        Thread.sleep(SLEEP);
        Mockito.verify(templateListenerFactory, Mockito.times(1)).create();
        templateListener.stop();
    }

    @Test
    public void readTest() throws InterruptedException {
        MockConsumer<String, RuleTemplate> mockConsumer = initMockConsumer();
        Mockito.when(templateListenerFactory.create()).thenReturn(mockConsumer);
        new Thread(() -> templateListener.run()).start();

        addRecord(mockConsumer, 0L, "0");
        Mockito.verify(templateDispatcherImpl, Mockito.times(1)).doDispatch(any());

        addRecord(mockConsumer, 1L, "1");
        Mockito.verify(templateDispatcherImpl, Mockito.times(2)).doDispatch(any());

        templateListener.stop();
    }

    private void addRecord(MockConsumer<String, RuleTemplate> mockConsumer, long l, String s) throws InterruptedException {
        RuleTemplate ruleTemplate = new RuleTemplate();
        mockConsumer.addRecord(new ConsumerRecord<>(MY_TOPIC, 0, l, s, ruleTemplate));
        Thread.sleep(SLEEP);
    }

    @Test(expected = IllegalStateException.class)
    public void stop() throws InterruptedException {
        MockConsumer<String, RuleTemplate> mockConsumer = initMockConsumer();
        Mockito.when(templateListenerFactory.create()).thenReturn(mockConsumer);

        new Thread(() -> templateListener.run()).start();
        Thread.sleep(SLEEP);
        templateListener.stop();
        Thread.sleep(SLEEP);
        RuleTemplate ruleTemplateNew = new RuleTemplate();
        mockConsumer.addRecord(new ConsumerRecord<>(MY_TOPIC, 0, 1L, "1", ruleTemplateNew));
    }
}