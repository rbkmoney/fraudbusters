package com.rbkmoney.fraudbusters.config;

import com.rbkmoney.damsel.fraudbusters.Command;
import com.rbkmoney.fraudbusters.domain.FraudResult;
import com.rbkmoney.fraudbusters.serde.CommandDeserializer;
import com.rbkmoney.fraudbusters.serde.FraudRequestSerde;
import com.rbkmoney.fraudbusters.serde.FraudoResultDeserializer;
import com.rbkmoney.fraudbusters.util.KeyGenerator;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.config.SslConfigs;
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
import org.springframework.kafka.listener.LoggingErrorHandler;
import org.springframework.retry.RetryPolicy;
import org.springframework.retry.backoff.BackOffPolicy;
import org.springframework.retry.backoff.ExponentialBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@Configuration
public class KafkaConfig {

    private static final String TEMPLATE_GROUP_ID = "TemplateListener-";
    private static final String REFERENCE_GROUP_ID = "ReferenceListener-";
    private static final String EARLIEST = "earliest";
    private static final String RESULT_AGGREGATOR = "ResultAggregator";
    private static final String MAX_POLL_RECORDS_CONFIG = "100";
    private static final String PKCS_12 = "PKCS12";
    private static final String SSL = "SSL";
    private static final String FRAUD_BUSTERS = "fraud-busters";
    private static final String FRAUD_BUSTERS_CLIENT = "fraud-busters-client";

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

    @Bean
    public Properties fraudStreamProperties() {
        final Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, FRAUD_BUSTERS);
        props.put(StreamsConfig.CLIENT_ID_CONFIG, FRAUD_BUSTERS_CLIENT);
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, FraudRequestSerde.class);
        props.put(StreamsConfig.COMMIT_INTERVAL_MS_CONFIG, 10 * 1000);
        props.put(StreamsConfig.CACHE_MAX_BYTES_BUFFERING_CONFIG, 0);
        props.putAll(sslConfigure());
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
        props.putAll(sslConfigure());
        return props;
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, Command> templateListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, Command> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(templateListenerFactory());
        factory.setConcurrency(1);
        factory.setRetryTemplate(retryTemplate());
        factory.setErrorHandler(new LoggingErrorHandler());
        return factory;
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, Command> referenceListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, Command> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(referenceListenerFactory());
        factory.setConcurrency(1);
        factory.setRetryTemplate(retryTemplate());
        factory.setErrorHandler(new LoggingErrorHandler());
        return factory;
    }

    /*
     * Retry template.
     */
    private RetryPolicy retryPolicy() {
        SimpleRetryPolicy policy = new SimpleRetryPolicy();
        policy.setMaxAttempts(3);
        return policy;
    }

    private BackOffPolicy backOffPolicy() {
        ExponentialBackOffPolicy policy = new ExponentialBackOffPolicy();
        policy.setInitialInterval(1000);
        return policy;
    }

    private RetryTemplate retryTemplate() {
        RetryTemplate template = new RetryTemplate();
        template.setRetryPolicy(retryPolicy());
        template.setBackOffPolicy(backOffPolicy());
        return template;
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

    private Map<String, Object> sslConfigure() {
        Map<String, Object> configProps = new HashMap<>();
        if (kafkaSslEnable) {
            configProps.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, SSL);
            configProps.put(SslConfigs.SSL_TRUSTSTORE_LOCATION_CONFIG, new File(serverStoreCertPath).getAbsolutePath());
            configProps.put(SslConfigs.SSL_TRUSTSTORE_PASSWORD_CONFIG, serverStorePassword);
            configProps.put(SslConfigs.SSL_KEYSTORE_TYPE_CONFIG, PKCS_12);
            configProps.put(SslConfigs.SSL_TRUSTSTORE_TYPE_CONFIG, PKCS_12);
            configProps.put(SslConfigs.SSL_KEYSTORE_LOCATION_CONFIG, new File(clientStoreCertPath).getAbsolutePath());
            configProps.put(SslConfigs.SSL_KEYSTORE_PASSWORD_CONFIG, keyStorePassword);
            configProps.put(SslConfigs.SSL_KEY_PASSWORD_CONFIG, keyPassword);
        }
        return configProps;
    }
}
