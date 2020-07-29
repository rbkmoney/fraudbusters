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
public class P2pTemplateListenersConfig {

    private final ListenersConfigurationService listenersConfigurationService;

    @Bean
    public ConsumerFactory<String, Command> templateP2PListenerFactory() {
        return listenersConfigurationService.createDefaultConsumerFactory(GroupPostfix.TEMPLATE_P2P_GROUP_ID);
    }

    @Bean
    public ConsumerFactory<String, Command> groupP2PListenerFactory() {
        return listenersConfigurationService.createDefaultConsumerFactory(GroupPostfix.GROUP_P2P_LIST_GROUP_ID);
    }

    @Bean
    public ConsumerFactory<String, Command> referenceP2PListenerFactory() {
        return listenersConfigurationService.createDefaultConsumerFactory(GroupPostfix.REFERENCE_P2P_GROUP_ID);
    }

    @Bean
    public ConsumerFactory<String, Command> groupReferenceP2PListenerFactory() {
        return listenersConfigurationService.createDefaultConsumerFactory(GroupPostfix.GROUP_P2P_LIST_REFERENCE_GROUP_ID);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, Command> templateP2PListenerContainerFactory(
            ConsumerFactory<String, Command> templateP2PListenerFactory) {
        return listenersConfigurationService.createDefaultFactory(templateP2PListenerFactory);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, Command> groupP2PListenerContainerFactory(
            ConsumerFactory<String, Command> groupP2PListenerFactory) {
        return listenersConfigurationService.createDefaultFactory(groupP2PListenerFactory);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, Command> referenceP2PListenerContainerFactory(
            ConsumerFactory<String, Command> referenceP2PListenerFactory) {
        return listenersConfigurationService.createDefaultFactory(referenceP2PListenerFactory);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, Command> groupReferenceP2PListenerContainerFactory(
            ConsumerFactory<String, Command> groupReferenceP2PListenerFactory) {
        return listenersConfigurationService.createDefaultFactory(groupReferenceP2PListenerFactory);
    }

}
