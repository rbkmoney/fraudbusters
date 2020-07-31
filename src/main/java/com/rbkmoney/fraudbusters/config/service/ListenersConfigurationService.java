package com.rbkmoney.fraudbusters.config.service;

import com.rbkmoney.damsel.fraudbusters.Command;
import com.rbkmoney.fraudbusters.config.properties.KafkaSslProperties;
import com.rbkmoney.fraudbusters.serde.CommandDeserializer;
import com.rbkmoney.fraudbusters.service.ConsumerGroupIdService;
import com.rbkmoney.fraudbusters.util.SslKafkaUtils;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.LoggingErrorHandler;
import org.springframework.retry.RetryPolicy;
import org.springframework.retry.backoff.BackOffPolicy;
import org.springframework.retry.backoff.ExponentialBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ListenersConfigurationService {

    public static final long THROTTLING_TIMEOUT = 500L;

    private static final String EARLIEST = "earliest";

    @Value("${kafka.max.poll.records}")
    private String maxPollRecords;

    @Value("${kafka.max.retry.attempts}")
    private int maxRetryAttempts;

    @Value("${kafka.backoff.interval}")
    private int backoffInterval;

    @Value("${kafka.bootstrap.servers}")
    private String bootstrapServers;

    @Value("${kafka.listen.result.concurrency}")
    private int listenResultConcurrency;

    private final ConsumerGroupIdService consumerGroupIdService;
    private final KafkaSslProperties kafkaSslProperties;

    public Map<String, Object> createDefaultProperties(String value) {
        final Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, value);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, EARLIEST);
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, true);
        props.putAll(SslKafkaUtils.sslConfigure(kafkaSslProperties));
        return props;
    }

    public ConcurrentKafkaListenerContainerFactory<String, Command> createDefaultFactory(ConsumerFactory<String, Command> stringCommandConsumerFactory) {
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

    public <T> ConcurrentKafkaListenerContainerFactory<String, T> createFactory(Deserializer<T> deserializer, String groupId) {
        String consumerGroup = consumerGroupIdService.generateGroupId(groupId);
        final Map<String, Object> props = createDefaultProperties(consumerGroup);
        return createFactoryWithProps(deserializer, props);
    }

    public <T> ConcurrentKafkaListenerContainerFactory<String, T> createFactoryWithProps(Deserializer<T> deserializer, Map<String, Object> props) {
        ConcurrentKafkaListenerContainerFactory<String, T> factory = new ConcurrentKafkaListenerContainerFactory<>();
        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, maxPollRecords);
        DefaultKafkaConsumerFactory<String, T> consumerFactory = new DefaultKafkaConsumerFactory<>(props,
                new StringDeserializer(), deserializer);
        factory.setConsumerFactory(consumerFactory);
        factory.setConcurrency(listenResultConcurrency);
        factory.setBatchListener(true);
        return factory;
    }

    public ConsumerFactory<String, Command> createDefaultConsumerFactory(String groupListReferenceGroupId) {
        String value = consumerGroupIdService.generateRandomGroupId(groupListReferenceGroupId);
        final Map<String, Object> props = createDefaultProperties(value);
        return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), new CommandDeserializer());
    }

}
