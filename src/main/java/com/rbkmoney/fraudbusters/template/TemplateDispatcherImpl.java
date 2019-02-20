package com.rbkmoney.fraudbusters.template;

import com.rbkmoney.fraudbusters.constant.CommandType;
import com.rbkmoney.fraudbusters.domain.RuleTemplate;
import com.rbkmoney.fraudbusters.fraud.FraudContextParser;
import com.rbkmoney.fraudbusters.template.pool.RuleTemplatePool;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class TemplateDispatcherImpl implements TemplateDispatcher {

    private final FraudContextParser fraudContextParser;
    private final RuleTemplatePool templatePool;

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
                return new DeleteCommand(templatePool);
            case UPDATE:
                return new UpdateCommand(fraudContextParser, templatePool);
            default:
                log.warn("Unknown command: {}", commandType);
                throw new RuntimeException("Unknown command!");
        }
    }

}
