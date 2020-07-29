package com.rbkmoney.fraudbusters.config.listeners;

import com.rbkmoney.damsel.fraudbusters.Command;
import com.rbkmoney.fraudbusters.constant.GroupPostfix;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;

@Configuration
@RequiredArgsConstructor
public class PaymentTemplateListenersConfig {

    private final ListenersConfigurationService listenersConfigurationService;

    @Bean
    public ConsumerFactory<String, Command> templateListenerFactory() {
        return listenersConfigurationService.createDefaultConsumerFactory(GroupPostfix.TEMPLATE_GROUP_ID);
    }

    @Bean
    public ConsumerFactory<String, Command> groupReferenceListenerFactory() {
        return listenersConfigurationService.createDefaultConsumerFactory(GroupPostfix.GROUP_LIST_REFERENCE_GROUP_ID);
    }

    @Bean
    public ConsumerFactory<String, Command> referenceListenerFactory() {
        return listenersConfigurationService.createDefaultConsumerFactory(GroupPostfix.REFERENCE_GROUP_ID);
    }

    @Bean
    public ConsumerFactory<String, Command> groupListenerFactory() {
        return listenersConfigurationService.createDefaultConsumerFactory(GroupPostfix.GROUP_LIST_GROUP_ID);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, Command> groupListenerContainerFactory(
            ConsumerFactory<String, Command> groupListenerFactory) {
        return listenersConfigurationService.createDefaultFactory(groupListenerFactory);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, Command> templateListenerContainerFactory(
            ConsumerFactory<String, Command> templateListenerFactory) {
        return listenersConfigurationService.createDefaultFactory(templateListenerFactory);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, Command> referenceListenerContainerFactory(
            ConsumerFactory<String, Command> referenceListenerFactory) {
        return listenersConfigurationService.createDefaultFactory(referenceListenerFactory);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, Command> groupReferenceListenerContainerFactory(
            ConsumerFactory<String, Command> groupReferenceListenerFactory) {
        return listenersConfigurationService.createDefaultFactory(groupReferenceListenerFactory);
    }

}
