package com.rbkmoney.fraudbusters.config.payment;

import com.rbkmoney.damsel.proxy_inspector.InspectorProxySrv;
import com.rbkmoney.fraudbusters.converter.CheckedResultToRiskScoreConverter;
import com.rbkmoney.fraudbusters.converter.ContextToFraudRequestConverter;
import com.rbkmoney.fraudbusters.domain.FraudResult;
import com.rbkmoney.fraudbusters.resource.payment.handler.FraudInspectorHandler;
import com.rbkmoney.fraudbusters.stream.impl.TemplateVisitorImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;

@Configuration
public class PaymentResourceConfig {

    @Value("${kafka.topic.result}")
    private String resultTopic;

    @Bean
    public InspectorProxySrv.Iface fraudInspectorHandler(
            KafkaTemplate<String, FraudResult> kafkaFraudResultTemplate,
            CheckedResultToRiskScoreConverter checkedResultToRiskScoreConverter,
            ContextToFraudRequestConverter requestConverter,
            TemplateVisitorImpl templateVisitor) {
        return new FraudInspectorHandler(
                resultTopic,
                checkedResultToRiskScoreConverter,
                requestConverter,
                templateVisitor,
                kafkaFraudResultTemplate
        );
    }

}
