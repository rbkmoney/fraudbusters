package com.rbkmoney.fraudbusters.config;

import com.rbkmoney.fraudbusters.domain.FraudResult;
import com.rbkmoney.fraudbusters.serde.FraudoModelSerializer;
import com.rbkmoney.fraudbusters.serde.FraudoResultDeserializer;
import com.rbkmoney.fraudo.model.FraudModel;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class ReplyTemplateConfig {

    private static final String REPLY_CONSUMER = "ReplyConsumer";

    @Value("${kafka.bootstrap.servers}")
    private String bootstrapServers;

    @Value("${kafka.result.stream.topic}")
    private String replyTopic;

    @Bean
    public Map<String, Object> producerConfigs() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, FraudoModelSerializer.class);
        return props;
    }

    @Bean
    public Map<String, Object> consumerConfigs() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, REPLY_CONSUMER);
        return props;
    }

    @Bean
    public ProducerFactory<String, FraudModel> producerFactory() {
        return new DefaultKafkaProducerFactory<>(producerConfigs());
    }

    @Bean
    public KafkaTemplate<String, FraudModel> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }


    @Bean
    public KafkaMessageListenerContainer<String, FraudResult> replyContainer() {
        ContainerProperties containerProperties = new ContainerProperties(replyTopic);
        return new KafkaMessageListenerContainer<>(consumerFactory(), containerProperties);
    }

    @Bean
    public ConsumerFactory<String, FraudResult> consumerFactory() {
        return new DefaultKafkaConsumerFactory<>(consumerConfigs(), new StringDeserializer(), new FraudoResultDeserializer());
    }

    @Bean
    public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String, FraudResult>> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, FraudResult> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        factory.setReplyTemplate(kafkaTemplate());
        return factory;
    }

    @Bean
    public ReplyingKafkaTemplate<String, FraudModel, FraudResult> replyKafkaTemplate(ProducerFactory<String, FraudModel> pf,
                                                                                     KafkaMessageListenerContainer<String,
                                                                                             FraudResult> container) {
        ReplyingKafkaTemplate<String, FraudModel, FraudResult> replyingKafkaTemplate = new ReplyingKafkaTemplate<>(pf, container);
        replyingKafkaTemplate.setReplyTimeout(30000L);
        return replyingKafkaTemplate;
    }

}
