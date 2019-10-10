package com.rbkmoney.fraudbusters.config;

import com.rbkmoney.damsel.proxy_inspector.InspectorProxySrv;
import com.rbkmoney.fraudbusters.converter.ContextToFraudRequestConverter;
import com.rbkmoney.fraudbusters.converter.FraudResultRiskScoreConverter;
import com.rbkmoney.fraudbusters.domain.FraudResult;
import com.rbkmoney.fraudbusters.resource.handler.FraudInspectorHandler;
import com.rbkmoney.fraudbusters.stream.TemplateVisitorImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;

@Configuration
public class ResourceConfig {

    @Value("${kafka.topic.result}")
    private String requestReplyTopic;

    @Bean
    @Autowired
    public InspectorProxySrv.Iface fraudInspectorHandler(KafkaTemplate<String, FraudResult> kafkaFraudResultTemplate,
                                                         FraudResultRiskScoreConverter resultConverter,
                                                         ContextToFraudRequestConverter requestConverter,
                                                         TemplateVisitorImpl templateVisitor) {
        return new FraudInspectorHandler(requestReplyTopic, resultConverter, requestConverter, templateVisitor, kafkaFraudResultTemplate);
    }

}
