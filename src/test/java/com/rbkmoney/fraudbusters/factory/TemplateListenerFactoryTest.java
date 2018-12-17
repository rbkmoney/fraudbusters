package com.rbkmoney.fraudbusters.factory;

import com.rbkmoney.fraudbusters.config.KafkaConfig;
import com.rbkmoney.fraudbusters.domain.RuleTemplate;
import org.apache.kafka.clients.consumer.Consumer;
import org.junit.Assert;
import org.junit.Test;

public class TemplateListenerFactoryTest {

    KafkaConfig kafkaConfig = new KafkaConfig("localhost:9092");

    @Test
    public void create() {
        TemplateListenerFactory templateListenerFactory = new TemplateListenerFactory("test",
                kafkaConfig.templateListenerProperties());
        Consumer<String, RuleTemplate> consumer = templateListenerFactory.create();
        Assert.assertNotNull(consumer);
    }
}