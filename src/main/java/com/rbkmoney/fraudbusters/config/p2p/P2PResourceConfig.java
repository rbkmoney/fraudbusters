package com.rbkmoney.fraudbusters.config.p2p;

import com.rbkmoney.fraudbusters.converter.CheckedResultToRiskScoreConverter;
import com.rbkmoney.fraudbusters.converter.P2PContextToP2PModelConverter;
import com.rbkmoney.fraudbusters.domain.ScoresResult;
import com.rbkmoney.fraudbusters.fraud.model.P2PModel;
import com.rbkmoney.fraudbusters.resource.p2p.handler.FraudP2PInspectorHandler;
import com.rbkmoney.fraudbusters.stream.impl.P2PTemplateVisitorImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;

@Configuration
public class P2PResourceConfig {

    @Value("${kafka.topic.p2p-result}")
    private String requestP2PResultTopic;

    @Bean
    public com.rbkmoney.damsel.p2p_insp.InspectorProxySrv.Iface fraudP2PInspectorHandler(KafkaTemplate<String, ScoresResult<P2PModel>> p2PModelKafkaTemplate,
                                                                                         CheckedResultToRiskScoreConverter resultConverter,
                                                                                         P2PContextToP2PModelConverter requestConverter,
                                                                                         P2PTemplateVisitorImpl templateListVisitor) {
        return new FraudP2PInspectorHandler(requestP2PResultTopic, resultConverter, requestConverter, templateListVisitor, p2PModelKafkaTemplate);
    }

}
