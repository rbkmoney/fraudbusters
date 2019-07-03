package com.rbkmoney.fraudbusters.config;

import com.rbkmoney.damsel.proxy_inspector.InspectorProxySrv;
import com.rbkmoney.fraudbusters.converter.ContextToFraudRequestConverter;
import com.rbkmoney.fraudbusters.converter.FraudResultRiskScoreConverter;
import com.rbkmoney.fraudbusters.domain.FraudRequest;
import com.rbkmoney.fraudbusters.domain.FraudResult;
import com.rbkmoney.fraudbusters.resource.handler.FraudInspectorHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;

@Configuration
public class ResourceConfig {

    @Value("${kafka.topic.global}")
    private String requestTopic;

    @Value("${kafka.topic.result}")
    private String requestReplyTopic;

    @Bean
    @Autowired
    public InspectorProxySrv.Iface fraudInspectorHandler(ReplyingKafkaTemplate<String, FraudRequest, FraudResult> kafkaTemplate,
                                                         FraudResultRiskScoreConverter resultConverter,
                                                         ContextToFraudRequestConverter requestConverter) {
        return new FraudInspectorHandler(kafkaTemplate, requestTopic, requestReplyTopic, resultConverter, requestConverter);
    }

}
