package com.rbkmoney.fraudbusters.config;

import com.rbkmoney.damsel.fraudbusters.Command;
import com.rbkmoney.fraudbusters.domain.FraudResult;
import com.rbkmoney.fraudbusters.serde.CommandDeserializer;
import com.rbkmoney.fraudbusters.serde.FraudRequestSerde;
import com.rbkmoney.fraudbusters.serde.FraudoResultDeserializer;
import com.rbkmoney.fraudbusters.util.KeyGenerator;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.streams.StreamsConfig;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@Configuration
public class KafkaConfig {

    private static final String TEMPLATE_GROUP_ID = "TemplateListener-";
    private static final String REFERENCE_GROUP_ID = "ReferenceListener-";
    private static final String EARLIEST = "earliest";
    private static final String RESULT_AGGREGATOR = "ResultAggregator";
    private static final String MAX_POLL_RECORDS_CONFIG = "20";

    @Value("${kafka.bootstrap.servers}")
    private String bootstrapServers;

    @Bean
    public Properties fraudStreamProperties() {
        final Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "fraud-busters");
        props.put(StreamsConfig.CLIENT_ID_CONFIG, "fraud-busters-client");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, FraudRequestSerde.class);
        props.put(StreamsConfig.COMMIT_INTERVAL_MS_CONFIG, 10 * 1000);
        props.put(StreamsConfig.CACHE_MAX_BYTES_BUFFERING_CONFIG, 0);
        return props;
    }

    @Bean
    public ConsumerFactory<String, Command> templateListenerFactory() {
        String value = KeyGenerator.generateKey(TEMPLATE_GROUP_ID);
        final Map<String, Object> props = createDefaultProperties(value);
        return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), new CommandDeserializer());
    }

    @Bean
    public ConsumerFactory<String, Command> referenceListenerFactory() {
        String value = KeyGenerator.generateKey(REFERENCE_GROUP_ID);
        final Map<String, Object> props = createDefaultProperties(value);
        return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), new CommandDeserializer());
    }

    @NotNull
    private Map<String, Object> createDefaultProperties(String value) {
        final Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, value);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, EARLIEST);
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, true);
        return props;
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, Command> templateListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, Command> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(templateListenerFactory());
        return factory;
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, Command> referenceListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, Command> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(referenceListenerFactory());
        return factory;
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, FraudResult> resultListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, FraudResult> factory = new ConcurrentKafkaListenerContainerFactory<>();
        final Map<String, Object> props = createDefaultProperties(RESULT_AGGREGATOR);
        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, MAX_POLL_RECORDS_CONFIG);
        DefaultKafkaConsumerFactory<String, FraudResult> consumerFactory = new DefaultKafkaConsumerFactory<>(props,
                new StringDeserializer(), new FraudoResultDeserializer());
        factory.setConsumerFactory(consumerFactory);
        return factory;
    }
}
