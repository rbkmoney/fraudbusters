package com.rbkmoney.fraudbusters.extension.config;

import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaAdmin;

import java.util.HashMap;
import java.util.Map;

@TestConfiguration
public class KafkaTopicsConfig {

    @Value("${kafka.bootstrap.servers}")
    private String bootstrapServer;

    @Bean
    public KafkaAdmin adminClient() {
        Map<String, Object> configs = new HashMap<>();
        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServer);
        return new KafkaAdmin(configs);
    }

    @Bean
    public NewTopic wbTopic() {
        return createTopic("wb-list-event-sink");
    }

    @Bean
    public NewTopic resultTopic() {
        return createTopic("result");
    }

    @Bean
    public NewTopic fraudPaymentTopic() {
        return createTopic("fraud_payment");
    }

    @Bean
    public NewTopic paymentEventTopic() {
        return createTopic("payment_event");
    }

    @Bean
    public NewTopic refundEventTopic() {
        return createTopic("refund_event");
    }

    @Bean
    public NewTopic chargebackEventTopic() {
        return createTopic("chargeback_event");
    }

    @Bean
    public NewTopic templateTopic() {
        return createTopic("template");
    }

    @Bean
    public NewTopic fullTemplateTopic() {
        return createTopic("full_template");
    }

    @Bean
    public NewTopic templateReferenceTopic() {
        return createTopic("template_reference");
    }

    @Bean
    public NewTopic fullTemplateReferenceTopic() {
        return createTopic("full_template_reference");
    }

    @Bean
    public NewTopic groupListTopic() {
        return createTopic("group_list");
    }

    @Bean
    public NewTopic fullGroupListTopic() {
        return createTopic("full_group_list");
    }

    @Bean
    public NewTopic groupReferenceTopic() {
        return createTopic("group_reference");
    }

    @Bean
    public NewTopic fullGroupReferenceListTopic() {
        return createTopic("full_group_reference");
    }

    private NewTopic createTopic(String name) {
        return TopicBuilder.name(name)
                .partitions(1)
                .replicas(1)
                .build();
    }

}
