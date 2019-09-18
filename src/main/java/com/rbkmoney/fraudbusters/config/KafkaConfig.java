package com.rbkmoney.fraudbusters.config;

import com.rbkmoney.damsel.fraudbusters.Command;
import com.rbkmoney.fraudbusters.domain.FraudResult;
import com.rbkmoney.fraudbusters.serde.*;
import com.rbkmoney.fraudbusters.service.ConsumerGroupIdService;
import com.rbkmoney.fraudbusters.util.SslKafkaUtils;
import lombok.RequiredArgsConstructor;
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
import org.springframework.kafka.listener.LoggingErrorHandler;
import org.springframework.retry.RetryPolicy;
import org.springframework.retry.backoff.BackOffPolicy;
import org.springframework.retry.backoff.ExponentialBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@Configuration
@RequiredArgsConstructor
public class KafkaConfig {

    private static final String TEMPLATE_GROUP_ID = "template-listener";
    private static final String GROUP_LIST_GROUP_ID = "group-listener";
    private static final String GROUP_LIST_REFERENCE_GROUP_ID = "group-reference-listener";
    private static final String REFERENCE_GROUP_ID = "reference-listener";
    private static final String EARLIEST = "earliest";
    private static final String RESULT_AGGREGATOR = "result-aggregator";
    private static final String MG_EVENT_SINK_AGGREGATOR = "mg-event-sink-aggregator";

    private static final String FRAUD_BUSTERS_CLIENT = "fraud-busters-client";
    public static final String APP_POSTFIX = "app";
    public static final String EVENT_SINK_CLIENT_FRAUDBUSTERS = "event-sink-client-fraudbusters";

    @Value("${kafka.max.poll.records}")
    private String maxPollRecords;

    @Value("${kafka.max.retry.attempts}")
    private int maxRetryAttempts;

    @Value("${kafka.backoff.interval}")
    private int backoffInterval;

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

    private final ConsumerGroupIdService consumerGroupIdService;

    @Bean
    public Properties fraudStreamProperties() {
        final Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, consumerGroupIdService.generateGroupId(APP_POSTFIX));
        props.put(StreamsConfig.CLIENT_ID_CONFIG, FRAUD_BUSTERS_CLIENT);
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, FraudRequestSerde.class);
        props.put(StreamsConfig.COMMIT_INTERVAL_MS_CONFIG, 10 * 1000);
        props.put(StreamsConfig.CACHE_MAX_BYTES_BUFFERING_CONFIG, 0);
        props.putAll(SslKafkaUtils.sslConfigure(kafkaSslEnable, serverStoreCertPath, serverStorePassword,
                clientStoreCertPath, keyStorePassword, keyPassword));
        return props;
    }

    @Bean
    public Properties eventSinkStreamProperties() {
        final Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, consumerGroupIdService.generateGroupId("event-sink-fraud"));
        props.put(StreamsConfig.CLIENT_ID_CONFIG, EVENT_SINK_CLIENT_FRAUDBUSTERS);
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, MgEventSinkRowSerde.class);
        props.put(StreamsConfig.COMMIT_INTERVAL_MS_CONFIG, 10 * 1000);
        props.put(StreamsConfig.CACHE_MAX_BYTES_BUFFERING_CONFIG, 0);
        props.putAll(SslKafkaUtils.sslConfigure(kafkaSslEnable, serverStoreCertPath, serverStorePassword,
                clientStoreCertPath, keyStorePassword, keyPassword));
        return props;
    }

    @Bean
    public ConsumerFactory<String, Command> templateListenerFactory() {
        return createDefaultConsumerFactory(TEMPLATE_GROUP_ID);
    }

    @Bean
    public ConsumerFactory<String, Command> groupListenerFactory() {
        return createDefaultConsumerFactory(GROUP_LIST_GROUP_ID);
    }

    @Bean
    public ConsumerFactory<String, Command> referenceListenerFactory() {
        return createDefaultConsumerFactory(REFERENCE_GROUP_ID);
    }

    @Bean
    public ConsumerFactory<String, Command> groupReferenceListenerFactory() {
        return createDefaultConsumerFactory(GROUP_LIST_REFERENCE_GROUP_ID);
    }

    @NotNull
    private ConsumerFactory<String, Command> createDefaultConsumerFactory(String groupListReferenceGroupId) {
        String value = consumerGroupIdService.generateRandomGroupId(groupListReferenceGroupId);
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
        props.putAll(SslKafkaUtils.sslConfigure(kafkaSslEnable, serverStoreCertPath, serverStorePassword,
                clientStoreCertPath, keyStorePassword, keyPassword));
        return props;
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, Command> templateListenerContainerFactory() {
        return createDefaultFactory(templateListenerFactory());
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, Command> groupListenerContainerFactory() {
        return createDefaultFactory(templateListenerFactory());
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, Command> referenceListenerContainerFactory() {
        return createDefaultFactory(referenceListenerFactory());
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, Command> referenceGroupListenerContainerFactory() {
        return createDefaultFactory(referenceListenerFactory());
    }

    @NotNull
    private ConcurrentKafkaListenerContainerFactory<String, Command> createDefaultFactory(ConsumerFactory<String, Command> stringCommandConsumerFactory) {
        ConcurrentKafkaListenerContainerFactory<String, Command> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(stringCommandConsumerFactory);
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
        policy.setMaxAttempts(maxRetryAttempts);
        return policy;
    }

    private BackOffPolicy backOffPolicy() {
        ExponentialBackOffPolicy policy = new ExponentialBackOffPolicy();
        policy.setInitialInterval(backoffInterval);
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
        String consumerGroup = consumerGroupIdService.generateGroupId(RESULT_AGGREGATOR);
        final Map<String, Object> props = createDefaultProperties(consumerGroup);
        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, maxPollRecords);
        DefaultKafkaConsumerFactory<String, FraudResult> consumerFactory = new DefaultKafkaConsumerFactory<>(props,
                new StringDeserializer(), new FraudoResultDeserializer());
        factory.setConsumerFactory(consumerFactory);
        return factory;
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, FraudResult> mgEventSinkListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, FraudResult> factory = new ConcurrentKafkaListenerContainerFactory<>();
        String consumerGroup = consumerGroupIdService.generateGroupId(MG_EVENT_SINK_AGGREGATOR);
        final Map<String, Object> props = createDefaultProperties(consumerGroup);
        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, maxPollRecords);
        DefaultKafkaConsumerFactory<String, FraudResult> consumerFactory = new DefaultKafkaConsumerFactory<>(props,
                new StringDeserializer(), new FraudoResultDeserializer());
        factory.setConsumerFactory(consumerFactory);
        return factory;
    }

}
