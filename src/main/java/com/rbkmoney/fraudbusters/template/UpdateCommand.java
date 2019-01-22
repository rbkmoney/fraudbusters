package com.rbkmoney.fraudbusters.template;

import com.rbkmoney.fraudbusters.constant.TemplateLevel;
import com.rbkmoney.fraudbusters.domain.RuleTemplate;
import com.rbkmoney.fraudbusters.fraud.FraudContextParser;
import com.rbkmoney.fraudbusters.stream.ConcreteTemplateStreamFactory;
import com.rbkmoney.fraudbusters.stream.GlobalStreamFactory;
import com.rbkmoney.fraudbusters.template.pool.StreamPool;
import com.rbkmoney.fraudbusters.util.KeyGenerator;
import com.rbkmoney.fraudo.FraudoParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsConfig;

import java.util.Properties;

@Slf4j
@RequiredArgsConstructor
public class UpdateCommand implements TemplateCommandInterface {

    private final Properties fraudStreamProperties;
    private final GlobalStreamFactory globalStreamFactory;
    private final ConcreteTemplateStreamFactory concreteTemplateStreamFactory;
    private final FraudContextParser fraudContextParser;
    private final StreamPool pool;

    @Override
    public void execute(RuleTemplate ruleTemplate) {
        TemplateLevel lvl = ruleTemplate.getLvl();
        switch (lvl) {
            case GLOBAL: {
                FraudoParser.ParseContext parseContext = fraudContextParser.parse(ruleTemplate.getTemplate());
                fraudStreamProperties.put(StreamsConfig.CLIENT_ID_CONFIG, KeyGenerator.generateKey("fraud-busters-global-stream-"));
                KafkaStreams newStream = globalStreamFactory.create(fraudStreamProperties, parseContext, pool);
                pool.add(TemplateLevel.GLOBAL.toString(), newStream);
                return;
            }
            case CONCRETE: {
                String localId = ruleTemplate.getLocalId();
                FraudoParser.ParseContext parseContext = fraudContextParser.parse(ruleTemplate.getTemplate());
                fraudStreamProperties.put(StreamsConfig.CLIENT_ID_CONFIG, KeyGenerator.generateKey("fraud-busters-concrete-stream-"));
                KafkaStreams streams = concreteTemplateStreamFactory.create(fraudStreamProperties, parseContext, localId);
                pool.add(localId, streams);
                return;
            }
            default: {
                log.warn("This template lvl={} is not supported!", lvl);
            }
        }
    }
}
