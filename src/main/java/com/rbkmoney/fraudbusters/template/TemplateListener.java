package com.rbkmoney.fraudbusters.template;

import com.rbkmoney.fraudbusters.domain.RuleTemplate;
import com.rbkmoney.fraudbusters.factory.TemplateListenerFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecords;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@RequiredArgsConstructor
public class TemplateListener implements Runnable {

    public static final long POLL_TIME = 100L;
    private AtomicBoolean stopped = new AtomicBoolean(false);

    private final TemplateDispatcherImpl templateDispatcherImpl;
    private final TemplateListenerFactory templateListenerFactory;

    @Override
    public void run() {
        final Consumer<String, RuleTemplate> consumer = templateListenerFactory.create();
        while (!stopped.get() && !Thread.currentThread().isInterrupted()) {
            final ConsumerRecords<String, RuleTemplate> consumerRecords = consumer.poll(Duration.ofMillis(POLL_TIME));
            consumerRecords.forEach(record -> templateDispatcherImpl.doDispatch(record.value()));
        }
        consumer.close();
    }

    public void stop() {
        stopped.set(true);
    }
}
