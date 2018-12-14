package com.rbkmoney.fraudbusters.template;

import com.rbkmoney.fraudbusters.domain.RuleTemplate;
import com.rbkmoney.fraudbusters.factory.stream.ConcreteStreamFactory;
import com.rbkmoney.fraudbusters.factory.stream.GlobalStreamFactory;
import com.rbkmoney.fraudo.FraudoLexer;
import com.rbkmoney.fraudo.FraudoParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.apache.kafka.streams.KafkaStreams;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@Slf4j
@Service
@RequiredArgsConstructor
public class TemplateBroker {

    private final Properties fraudStreamProperties;
    private final GlobalStreamFactory globalStreamFactory;
    private final ConcreteStreamFactory concreteStreamFactory;
    private KafkaStreams globalStreams;

    private Map<String, KafkaStreams> concreteStreams = new HashMap<>();

    public void doDispatch(RuleTemplate ruleTemplate) {
        switch (ruleTemplate.getLvl()) {
            case GLOBAL: {
                FraudoParser.ParseContext parseContext = getParseContext(ruleTemplate.getTemplate());
                KafkaStreams newStream = globalStreamFactory.create(fraudStreamProperties, parseContext);
                restartGlobalStream(globalStreams, newStream);
                globalStreams = newStream;
                return;
            }
            case CONCRETE: {
                String localId = ruleTemplate.getLocalId();
                KafkaStreams kafkaStreams = concreteStreams.get(localId);
                FraudoParser.ParseContext parseContext = getParseContext(ruleTemplate.getTemplate());
                KafkaStreams streams = concreteStreamFactory.create(fraudStreamProperties, parseContext);
                restartGlobalStream(kafkaStreams, streams);
                concreteStreams.put(localId, streams);
                return;
            }
            default: {
                log.warn("This template lvl={} is not supported!", ruleTemplate.getLvl());
            }
        }
    }

    private void restartGlobalStream(KafkaStreams kafkaStreamsOld, KafkaStreams newStream) {
        if (kafkaStreamsOld != null && kafkaStreamsOld.state().isRunning()) {
            kafkaStreamsOld.close();
        }
        while (true) {
            if (kafkaStreamsOld == null || !kafkaStreamsOld.state().isRunning()) {
                newStream.start();
                return;
            }
        }
    }

    private FraudoParser.ParseContext getParseContext(String template) {
        FraudoLexer lexer = new FraudoLexer(CharStreams.fromString(template));
        FraudoParser parser = new FraudoParser(new CommonTokenStream(lexer));
        return parser.parse();
    }

}
