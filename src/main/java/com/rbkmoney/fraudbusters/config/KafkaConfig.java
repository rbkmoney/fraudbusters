package com.rbkmoney.fraudbusters.config;

import com.rbkmoney.fraudbusters.domain.FraudResult;
import com.rbkmoney.fraudbusters.domain.RuleTemplate;
import com.rbkmoney.fraudbusters.serde.FraudoModelSerde;
import com.rbkmoney.fraudbusters.serde.FraudoModelSerializer;
import com.rbkmoney.fraudbusters.serde.FraudoResultDeserializer;
import com.rbkmoney.fraudbusters.serde.RuleTemplateDeserializer;
import com.rbkmoney.fraudbusters.util.KeyGenerator;
import com.rbkmoney.fraudo.model.FraudModel;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.kafka.streams.StreamsConfig;
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
import java.util.Properties;

@Configuration
public class KafkaConfig {

    private static final String GROUP_ID = "TemplateListener-";
    private static final String EARLIEST = "earliest";
    public static final String REPLY_CONSUMER = "ReplyConsumer";

    private final String bootstrapServers;
    private final String replyTopic;

    public KafkaConfig(@Value("${kafka.result.stream.topic}") String replyTopic,
                       @Value("${kafka.bootstrap.servers}") String bootstrapServers) {
        this.replyTopic = replyTopic;
        this.bootstrapServers = bootstrapServers;
    }

    @Bean
    public Properties fraudStreamProperties() {
        final Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "fraud-busters");
        props.put(StreamsConfig.CLIENT_ID_CONFIG, "fraud-busters-client");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, FraudoModelSerde.class);
        props.put(StreamsConfig.COMMIT_INTERVAL_MS_CONFIG, 10 * 1000);
        props.put(StreamsConfig.CACHE_MAX_BYTES_BUFFERING_CONFIG, 0);
        return props;
    }

    @Bean
    public ConsumerFactory<String, RuleTemplate> templateListenerFactory() {
        final Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, KeyGenerator.generateKey(GROUP_ID));
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, EARLIEST);
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, true);
        return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), new RuleTemplateDeserializer());
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, RuleTemplate> templateListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, RuleTemplate> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(templateListenerFactory());
        return factory;
    }

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
    public ReplyingKafkaTemplate<String, FraudModel, FraudResult> replyKafkaTemplate(ProducerFactory<String, FraudModel> pf,
                                                                                     KafkaMessageListenerContainer<String,
                                                                                             FraudResult> container) {
        return new ReplyingKafkaTemplate<>(pf, container);
    }

    @Bean
    public KafkaMessageListenerContainer<String, FraudResult> replyContainer(ConsumerFactory<String, FraudResult> cf) {
        ContainerProperties containerProperties = new ContainerProperties(replyTopic);
        return new KafkaMessageListenerContainer<>(cf, containerProperties);
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
}
