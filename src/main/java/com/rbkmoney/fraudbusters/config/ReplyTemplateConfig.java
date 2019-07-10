package com.rbkmoney.fraudbusters.config;

import com.rbkmoney.fraudbusters.domain.FraudRequest;
import com.rbkmoney.fraudbusters.domain.FraudResult;
import com.rbkmoney.fraudbusters.serde.FraudRequestSerializer;
import com.rbkmoney.fraudbusters.serde.FraudoResultDeserializer;
import com.rbkmoney.fraudbusters.service.ConsumerGroupIdService;
import com.rbkmoney.fraudbusters.util.SslKafkaUtils;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class ReplyTemplateConfig {

    private static final String REPLY_CONSUMER = "reply-consumer";

    @Value("${kafka.bootstrap.servers}")
    private String bootstrapServers;

    @Value("${kafka.ssl.server-password}")
    private String serverStorePassword;

    @Value("${kafka.ssl.server-keystore-location}")
    private String serverStoreCertPath;

    @Value("${kafka.ssl.keystore-password}")
    private String keyStorePassword;

    @Value("${kafka.ssl.key-password}")
    private String keyPassword;

    @Value("${kafka.ssl.keystore-location}")
    private String clientStoreCertPath;

    @Value("${kafka.ssl.enable}")
    private boolean kafkaSslEnable;

    @Value("${kafka.topic.result}")
    private String replyTopic;

    @Value("${kafka.reply.timeout}")
    private Long replyTimeout;

    private final ConsumerGroupIdService consumerGroupIdService;

    private Map<String, Object> producerConfigs() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, FraudRequestSerializer.class);
        props.putAll(SslKafkaUtils.sslConfigure(kafkaSslEnable, serverStoreCertPath, serverStorePassword,
                clientStoreCertPath, keyStorePassword, keyPassword));
        return props;
    }

    private Map<String, Object> consumerConfigs() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, consumerGroupIdService.generateGroupId(REPLY_CONSUMER));
        props.putAll(SslKafkaUtils.sslConfigure(kafkaSslEnable, serverStoreCertPath, serverStorePassword,
                clientStoreCertPath, keyStorePassword, keyPassword));
        return props;
    }

    public ProducerFactory<String, FraudRequest> producerFactory() {
        return new DefaultKafkaProducerFactory<>(producerConfigs());
    }

    @Bean
    public KafkaTemplate<String, FraudRequest> kafkaTemplate() {
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
    public ReplyingKafkaTemplate<String, FraudRequest, FraudResult> replyKafkaTemplate(KafkaMessageListenerContainer<String,
                                                                                             FraudResult> container) {
        ProducerFactory<String, FraudRequest> producerFactory = producerFactory();
        ReplyingKafkaTemplate<String, FraudRequest, FraudResult> replyingKafkaTemplate = new ReplyingKafkaTemplate<>(producerFactory, container);
        replyingKafkaTemplate.setReplyTimeout(replyTimeout);
        return replyingKafkaTemplate;
    }
}
