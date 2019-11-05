package com.rbkmoney.fraudbusters.config;

import com.rbkmoney.damsel.proxy_inspector.InspectorProxySrv;
import com.rbkmoney.fraudbusters.converter.CheckedResultToRiskScoreConverter;
import com.rbkmoney.fraudbusters.converter.ContextToFraudRequestConverter;
import com.rbkmoney.fraudbusters.converter.P2PContextToP2PModelConverter;
import com.rbkmoney.fraudbusters.domain.FraudResult;
import com.rbkmoney.fraudbusters.domain.ScoresResult;
import com.rbkmoney.fraudbusters.fraud.model.P2PModel;
import com.rbkmoney.fraudbusters.resource.handler.FraudInspectorHandler;
import com.rbkmoney.fraudbusters.resource.handler.FraudP2PInspectorHandler;
import com.rbkmoney.fraudbusters.stream.P2PTemplateVisitorImpl;
import com.rbkmoney.fraudbusters.stream.TemplateVisitorImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;

@Configuration
public class ResourceConfig {

    @Value("${kafka.topic.result}")
    private String requestReplyTopic;

    @Value("${kafka.topic.p2p.result}")
    private String requestP2PReplyTopic;

    @Bean
    public InspectorProxySrv.Iface fraudInspectorHandler(KafkaTemplate<String, FraudResult> kafkaFraudResultTemplate,
                                                         CheckedResultToRiskScoreConverter checkedResultToRiskScoreConverter,
                                                         ContextToFraudRequestConverter requestConverter,
                                                         TemplateVisitorImpl templateVisitor) {
        return new FraudInspectorHandler(requestReplyTopic, checkedResultToRiskScoreConverter, requestConverter, templateVisitor, kafkaFraudResultTemplate);
    }

    @Bean
    public com.rbkmoney.damsel.p2p_insp.InspectorProxySrv.Iface fraudP2PInspectorHandler(KafkaTemplate<String, ScoresResult<P2PModel>> p2PModelKafkaTemplate,
                                                                                         CheckedResultToRiskScoreConverter resultConverter,
                                                                                         P2PContextToP2PModelConverter requestConverter,
                                                                                         P2PTemplateVisitorImpl templateListVisitor) {
        return new FraudP2PInspectorHandler(requestP2PReplyTopic, resultConverter, requestConverter, templateListVisitor, p2PModelKafkaTemplate);
    }

}
