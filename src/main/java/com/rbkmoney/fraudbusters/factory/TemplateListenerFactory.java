package com.rbkmoney.fraudbusters.factory;

import com.rbkmoney.fraudbusters.domain.RuleTemplate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Properties;

@Slf4j
@Component
public class TemplateListenerFactory {

    private final String listenTopic;
    private final Properties templateListenerProperties;

    public TemplateListenerFactory(@Value("${template.topic}") String listenTopic, Properties templateListenerProperties) {
        this.listenTopic = listenTopic;
        this.templateListenerProperties = templateListenerProperties;
    }

    public Consumer<String, RuleTemplate> create() {
        final Consumer<String, RuleTemplate> consumer = new KafkaConsumer<>(templateListenerProperties);
        consumer.subscribe(Collections.singletonList(listenTopic));
        return consumer;
    }
}
