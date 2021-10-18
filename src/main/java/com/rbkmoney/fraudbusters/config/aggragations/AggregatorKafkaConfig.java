package com.rbkmoney.fraudbusters.config.aggragations;

import com.rbkmoney.damsel.fraudbusters.*;
import com.rbkmoney.fraudbusters.config.service.KafkaTemplateConfigurationService;
import com.rbkmoney.fraudbusters.config.service.ListenersConfigurationService;
import com.rbkmoney.fraudbusters.constant.GroupPostfix;
import com.rbkmoney.fraudbusters.domain.FraudResult;
import com.rbkmoney.fraudbusters.serde.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;

@SuppressWarnings("LineLength")
@Configuration
@RequiredArgsConstructor
public class AggregatorKafkaConfig {

    private final ListenersConfigurationService listenersConfigurationService;
    private final KafkaTemplateConfigurationService kafkaTemplateConfigurationService;
    @Value("${kafka.aggr.payment.min.bytes}")
    private int fetchMinBytes;

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, FraudResult> kafkaListenerContainerFactory() {
        return listenersConfigurationService.createFactory(
                new FraudResultDeserializer(),
                GroupPostfix.RESULT_AGGREGATOR
        );
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, Payment> kafkaPaymentResultListenerContainerFactory() {
        return listenersConfigurationService.createFactory(
                new PaymentDeserializer(),
                GroupPostfix.RESULT_AGGREGATOR,
                fetchMinBytes
        );
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, Payment> kafkaDgraphPaymentResultListenerContainerFactory() {
        return listenersConfigurationService.createDgraphFactory(
                new PaymentDeserializer(),
                "dgraph-payments",
                fetchMinBytes
        );
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, Withdrawal> kafkaWithdrawalResultListenerContainerFactory() {
        return listenersConfigurationService.createFactory(
                new WithdrawalDeserializer(),
                GroupPostfix.RESULT_AGGREGATOR,
                fetchMinBytes
        );
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, Refund> kafkaRefundResultListenerContainerFactory() {
        return listenersConfigurationService.createFactory(new RefundDeserializer(), GroupPostfix.RESULT_AGGREGATOR);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, Refund> kafkaDgraphRefundResultListenerContainerFactory() {
        return listenersConfigurationService.createDgraphFactory(
                new RefundDeserializer(),
                "dgraph-refunds",
                fetchMinBytes
        );
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, Chargeback> kafkaChargebackResultListenerContainerFactory() {
        return listenersConfigurationService.createFactory(
                new ChargebackDeserializer(),
                GroupPostfix.RESULT_AGGREGATOR
        );
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, Chargeback> kafkaDgraphChargebackResultListenerContainerFactory() {
        return listenersConfigurationService.createDgraphFactory(
                new ChargebackDeserializer(),
                "dgraph-chargebacks",
                fetchMinBytes
        );
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, FraudPayment> kafkaFraudPaymentListenerContainerFactory() {
        return listenersConfigurationService.createFactory(
                new FraudPaymentDeserializer(),
                GroupPostfix.RESULT_AGGREGATOR
        );
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, FraudPayment> kafkaDrgaphFraudPaymentListenerContainerFactory() {
        return listenersConfigurationService.createDgraphFactory(
                new FraudPaymentDeserializer(),
                "dgraph-fraud-payments",
                fetchMinBytes
        );
    }

    @Bean
    public KafkaTemplate<String, Payment> paymentKafkaTemplate() {
        return new KafkaTemplate<>(new DefaultKafkaProducerFactory<>(kafkaTemplateConfigurationService.producerThriftConfigs()));
    }

    @Bean
    public KafkaTemplate<String, Refund> refundKafkaTemplate() {
        return new KafkaTemplate<>(new DefaultKafkaProducerFactory<>(kafkaTemplateConfigurationService.producerThriftConfigs()));
    }

    @Bean
    public KafkaTemplate<String, Chargeback> chargebackKafkaTemplate() {
        return new KafkaTemplate<>(new DefaultKafkaProducerFactory<>(kafkaTemplateConfigurationService.producerThriftConfigs()));
    }

    @Bean
    public KafkaTemplate<String, FraudPayment> fraudPaymentKafkaTemplate() {
        return new KafkaTemplate<>(new DefaultKafkaProducerFactory<>(kafkaTemplateConfigurationService.producerThriftConfigs()));
    }

    @Bean
    public KafkaTemplate<String, Withdrawal> fraudWithdrawalKafkaTemplate() {
        return new KafkaTemplate<>(new DefaultKafkaProducerFactory<>(kafkaTemplateConfigurationService.producerThriftConfigs()));
    }

}
