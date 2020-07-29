package com.rbkmoney.fraudbusters.config;

import com.rbkmoney.damsel.fraudbusters.*;
import com.rbkmoney.fraudbusters.config.properties.KafkaSslProperties;
import com.rbkmoney.fraudbusters.constant.GroupPostfix;
import com.rbkmoney.fraudbusters.domain.FraudResult;
import com.rbkmoney.fraudbusters.domain.ScoresResult;
import com.rbkmoney.fraudbusters.fraud.model.P2PModel;
import com.rbkmoney.fraudbusters.serde.*;
import com.rbkmoney.fraudbusters.service.ConsumerGroupIdService;
import com.rbkmoney.fraudbusters.util.SslKafkaUtils;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
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

@Configuration
@RequiredArgsConstructor
public class KafkaConfig {

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

    @Bean
    public ConsumerFactory<String, Command> templateListenerFactory() {
        return createDefaultConsumerFactory(GroupPostfix.TEMPLATE_GROUP_ID);
    }

    @Bean
    public ConsumerFactory<String, Command> timeTemplateListenerFactory() {
        return createDefaultConsumerFactory(GroupPostfix.TEMPLATE_GROUP_ID);
    }

    @Bean
    public ConsumerFactory<String, Command> groupListenerFactory() {
        return createDefaultConsumerFactory(GroupPostfix.GROUP_LIST_GROUP_ID);
    }

    @Bean
    public ConsumerFactory<String, Command> timeGroupListenerFactory() {
        return createDefaultConsumerFactory(GroupPostfix.GROUP_LIST_GROUP_ID);
    }

    @Bean
    public ConsumerFactory<String, Command> referenceListenerFactory() {
        return createDefaultConsumerFactory(GroupPostfix.REFERENCE_GROUP_ID);
    }


    @Bean
    public ConsumerFactory<String, Command> timeReferenceListenerFactory() {
        return createDefaultConsumerFactory(GroupPostfix.REFERENCE_GROUP_ID);
    }

    @Bean
    public ConsumerFactory<String, Command> groupReferenceListenerFactory() {
        return createDefaultConsumerFactory(GroupPostfix.GROUP_LIST_REFERENCE_GROUP_ID);
    }

    @Bean
    public ConsumerFactory<String, Command> timeGroupReferenceListenerFactory() {
        return createDefaultConsumerFactory(GroupPostfix.GROUP_LIST_REFERENCE_GROUP_ID);
    }

    @Bean
    public ConsumerFactory<String, Command> templateP2PListenerFactory() {
        return createDefaultConsumerFactory(GroupPostfix.TEMPLATE_P2P_GROUP_ID);
    }

    @Bean
    public ConsumerFactory<String, Command> groupP2PListenerFactory() {
        return createDefaultConsumerFactory(GroupPostfix.GROUP_P2P_LIST_GROUP_ID);
    }

    @Bean
    public ConsumerFactory<String, Command> referenceP2PListenerFactory() {
        return createDefaultConsumerFactory(GroupPostfix.REFERENCE_P2P_GROUP_ID);
    }

    @Bean
    public ConsumerFactory<String, Command> groupReferenceP2PListenerFactory() {
        return createDefaultConsumerFactory(GroupPostfix.GROUP_P2P_LIST_REFERENCE_GROUP_ID);
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
        props.putAll(SslKafkaUtils.sslConfigure(kafkaSslProperties));
        return props;
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, Command> templateListenerContainerFactory(
            ConsumerFactory<String, Command> templateListenerFactory) {
        return createDefaultFactory(templateListenerFactory);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, Command> timeTemplateListenerContainerFactory(
            ConsumerFactory<String, Command> timeTemplateListenerFactory) {
        return createDefaultFactory(timeTemplateListenerFactory);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, Command> templateP2PListenerContainerFactory(
            ConsumerFactory<String, Command> templateP2PListenerFactory) {
        return createDefaultFactory(templateP2PListenerFactory);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, Command> groupListenerContainerFactory(
            ConsumerFactory<String, Command> groupListenerFactory) {
        return createDefaultFactory(groupListenerFactory);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, Command> timeGroupListenerContainerFactory(
            ConsumerFactory<String, Command> timeGroupListenerFactory) {
        return createDefaultFactory(timeGroupListenerFactory);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, Command> groupP2PListenerContainerFactory(
            ConsumerFactory<String, Command> groupP2PListenerFactory) {
        return createDefaultFactory(groupP2PListenerFactory);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, Command> referenceListenerContainerFactory(
            ConsumerFactory<String, Command> referenceListenerFactory) {
        return createDefaultFactory(referenceListenerFactory);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, Command> timeReferenceListenerContainerFactory(
            ConsumerFactory<String, Command> timeReferenceListenerFactory) {
        return createDefaultFactory(timeReferenceListenerFactory);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, Command> referenceP2PListenerContainerFactory(
            ConsumerFactory<String, Command> referenceP2PListenerFactory) {
        return createDefaultFactory(referenceP2PListenerFactory);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, Command> groupReferenceListenerContainerFactory(
            ConsumerFactory<String, Command> groupReferenceListenerFactory) {
        return createDefaultFactory(groupReferenceListenerFactory);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, Command> timeGroupReferenceListenerContainerFactory(
            ConsumerFactory<String, Command> timeGroupReferenceListenerFactory) {
        return createDefaultFactory(timeGroupReferenceListenerFactory);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, Command> groupReferenceP2PListenerContainerFactory(
            ConsumerFactory<String, Command> groupReferenceP2PListenerFactory) {
        return createDefaultFactory(groupReferenceP2PListenerFactory);
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
    public ConcurrentKafkaListenerContainerFactory<String, FraudResult> kafkaListenerContainerFactory() {
        return createFactory(new FraudResultDeserializer(), GroupPostfix.RESULT_AGGREGATOR);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, Payment> kafkaPaymentResultListenerContainerFactory() {
        return createFactory(new PaymentDeserializer(), GroupPostfix.PAYMENT_AGGREGATOR);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, Refund> kafkaRefundResultListenerContainerFactory() {
        return createFactory(new RefundDeserializer(), GroupPostfix.REFUND_AGGREGATOR);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, Chargeback> kafkaChargebackResultListenerContainerFactory() {
        return createFactory(new ChargebackDeserializer(), GroupPostfix.CHARGEBACK_AGGREGATOR);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, ScoresResult<P2PModel>> kafkaListenerP2PResultContainerFactory() {
        return createFactory(new P2PResultDeserializer(), GroupPostfix.P2P_RESULT_AGGREGATOR);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, FraudPayment> fraudPaymentListenerContainerFactory() {
        return createFactory(new FraudPaymentDeserializer(), GroupPostfix.FRAUD_PAYMENT_AGGREGATOR);
    }

    @NotNull
    private <T> ConcurrentKafkaListenerContainerFactory<String, T> createFactory(Deserializer<T> deserializer, String groupId) {
        String consumerGroup = consumerGroupIdService.generateGroupId(groupId);
        final Map<String, Object> props = createDefaultProperties(consumerGroup);
        return createFactoryWithProps(deserializer, props);
    }

    @NotNull
    private <T> ConcurrentKafkaListenerContainerFactory<String, T> createFactoryWithProps(Deserializer<T> deserializer, Map<String, Object> props) {
        ConcurrentKafkaListenerContainerFactory<String, T> factory = new ConcurrentKafkaListenerContainerFactory<>();
        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, maxPollRecords);
        DefaultKafkaConsumerFactory<String, T> consumerFactory = new DefaultKafkaConsumerFactory<>(props,
                new StringDeserializer(), deserializer);
        factory.setConsumerFactory(consumerFactory);
        factory.setConcurrency(listenResultConcurrency);
        factory.setBatchListener(true);
        return factory;
    }

}
