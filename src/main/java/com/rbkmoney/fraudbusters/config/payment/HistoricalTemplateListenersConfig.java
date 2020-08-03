package com.rbkmoney.fraudbusters.config.payment;

import com.rbkmoney.damsel.fraudbusters.Command;
import com.rbkmoney.fraudbusters.config.service.ListenersConfigurationService;
import com.rbkmoney.fraudbusters.constant.GroupPostfix;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;

@Configuration
@RequiredArgsConstructor
public class HistoricalTemplateListenersConfig {

    private final ListenersConfigurationService listenersConfigurationService;

    @Bean
    public ConsumerFactory<String, Command> timeReferenceListenerFactory() {
        return listenersConfigurationService.createDefaultConsumerFactory(GroupPostfix.REFERENCE_GROUP_ID);
    }

    @Bean
    public ConsumerFactory<String, Command> timeTemplateListenerFactory() {
        return listenersConfigurationService.createDefaultConsumerFactory(GroupPostfix.TEMPLATE_GROUP_ID);
    }

    @Bean
    public ConsumerFactory<String, Command> timeGroupListenerFactory() {
        return listenersConfigurationService.createDefaultConsumerFactory(GroupPostfix.GROUP_LIST_GROUP_ID);
    }

    @Bean
    public ConsumerFactory<String, Command> timeGroupReferenceListenerFactory() {
        return listenersConfigurationService.createDefaultConsumerFactory(GroupPostfix.GROUP_LIST_REFERENCE_GROUP_ID);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, Command> timeGroupListenerContainerFactory(
            ConsumerFactory<String, Command> timeGroupListenerFactory) {
        return listenersConfigurationService.createDefaultFactory(timeGroupListenerFactory);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, Command> timeReferenceListenerContainerFactory(
            ConsumerFactory<String, Command> timeReferenceListenerFactory) {
        return listenersConfigurationService.createDefaultFactory(timeReferenceListenerFactory);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, Command> timeTemplateListenerContainerFactory(
            ConsumerFactory<String, Command> timeTemplateListenerFactory) {
        return listenersConfigurationService.createDefaultFactory(timeTemplateListenerFactory);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, Command> timeGroupReferenceListenerContainerFactory(
            ConsumerFactory<String, Command> timeGroupReferenceListenerFactory) {
        return listenersConfigurationService.createDefaultFactory(timeGroupReferenceListenerFactory);
    }


}
