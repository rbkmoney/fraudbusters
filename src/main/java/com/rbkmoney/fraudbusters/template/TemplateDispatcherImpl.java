package com.rbkmoney.fraudbusters.template;

import com.rbkmoney.fraudbusters.constant.CommandType;
import com.rbkmoney.fraudbusters.domain.RuleTemplate;
import com.rbkmoney.fraudbusters.fraud.FraudContextParser;
import com.rbkmoney.fraudbusters.stream.ConcreteTemplateStreamFactory;
import com.rbkmoney.fraudbusters.stream.GlobalStreamFactory;
import com.rbkmoney.fraudbusters.template.pool.StreamPool;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
        if (RuleTemplateValidator.validate(ruleTemplate)) {
            TemplateCommandInterface templateCommandInterface = createCommand(ruleTemplate);
            templateCommandInterface.execute(ruleTemplate);
        }
    }

    private TemplateCommandInterface createCommand(RuleTemplate ruleTemplate) {
        CommandType commandType = ruleTemplate.getCommandType();
        switch (commandType) {
            case DELETE:
                return new DeleteCommand(pool);
            case UPDATE:
                return new UpdateCommand(fraudStreamProperties, globalStreamFactory,
                        concreteTemplateStreamFactory, fraudContextParser, pool);
            default:
                log.warn("Unknown command: {}", commandType);
                throw new RuntimeException("Unknown command!");
        }
    }

}
