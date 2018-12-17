package com.rbkmoney.fraudbusters.template;

import com.rbkmoney.fraudbusters.constant.Level;
import com.rbkmoney.fraudbusters.domain.RuleTemplate;
import com.rbkmoney.fraudbusters.factory.stream.ConcreteTemplateStreamFactory;
import com.rbkmoney.fraudbusters.factory.stream.GlobalStreamFactory;
import com.rbkmoney.fraudbusters.fraud.FraudContextParser;
import com.rbkmoney.fraudbusters.template.pool.StreamPool;
import com.rbkmoney.fraudo.FraudoParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.streams.KafkaStreams;
import org.springframework.stereotype.Service;

import java.util.Properties;

@Slf4j
@Service
@RequiredArgsConstructor
public class TemplateDispatcherImpl implements TemplateDispatcher {

    private final Properties fraudStreamProperties;
    private final GlobalStreamFactory globalStreamFactory;
    private final ConcreteTemplateStreamFactory concreteTemplateStreamFactory;
    private final FraudContextParser fraudContextParser;
    private final StreamPool pool;

    public void doDispatch(RuleTemplate ruleTemplate) {
        switch (ruleTemplate.getLvl()) {
            case GLOBAL: {
                FraudoParser.ParseContext parseContext = fraudContextParser.parse(ruleTemplate.getTemplate());
                KafkaStreams newStream = globalStreamFactory.create(fraudStreamProperties, parseContext);
                pool.add(Level.GLOBAL.toString(), newStream);
                return;
            }
            case CONCRETE: {
                String localId = ruleTemplate.getLocalId();
                FraudoParser.ParseContext parseContext = fraudContextParser.parse(ruleTemplate.getTemplate());
                KafkaStreams streams = concreteTemplateStreamFactory.create(fraudStreamProperties, parseContext, localId);
                pool.add(localId, streams);
                return;
            }
            default: {
                log.warn("This template lvl={} is not supported!", ruleTemplate.getLvl());
            }
        }
    }

}
