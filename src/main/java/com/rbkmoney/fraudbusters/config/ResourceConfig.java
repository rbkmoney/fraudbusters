package com.rbkmoney.fraudbusters.config;

import com.rbkmoney.damsel.proxy_inspector.InspectorProxySrv;
import com.rbkmoney.fraudbusters.converter.ContextToFraudModelConverter;
import com.rbkmoney.fraudbusters.converter.FraudResultRiskScoreConverter;
import com.rbkmoney.fraudbusters.domain.FraudResult;
import com.rbkmoney.fraudbusters.handler.FraudInspectorHandler;
import com.rbkmoney.fraudo.model.FraudModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;

@Configuration
public class ResourceConfig {

    @Value("${kafka.global.stream.topic}")
    private String requestTopic;

    @Value("${kafka.result.stream.topic}")
    private String requestReplyTopic;

    @Bean
    @Autowired
    public InspectorProxySrv.Iface fraudInspectorHandler(ReplyingKafkaTemplate<String, FraudModel, FraudResult> kafkaTemplate,
                                                         FraudResultRiskScoreConverter resultConverter,
                                                         ContextToFraudModelConverter requestConverter) {
        return new FraudInspectorHandler(kafkaTemplate, requestTopic, requestReplyTopic, resultConverter, requestConverter);
    }

}
