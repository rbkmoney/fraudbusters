package com.rbkmoney.fraudbusters.template;

import com.rbkmoney.fraudbusters.constant.TemplateLevel;
import com.rbkmoney.fraudbusters.domain.RuleTemplate;
import com.rbkmoney.fraudbusters.fraud.FraudContextParser;
import com.rbkmoney.fraudbusters.template.pool.RuleTemplatePool;
import com.rbkmoney.fraudo.FraudoParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class UpdateCommand implements TemplateCommandInterface {

    private final FraudContextParser fraudContextParser;
    private final RuleTemplatePool templatePool;

    @Override
    public void execute(RuleTemplate ruleTemplate) {
        TemplateLevel lvl = ruleTemplate.getLvl();
        switch (lvl) {
            case GLOBAL: {
                FraudoParser.ParseContext parseContext = fraudContextParser.parse(ruleTemplate.getTemplate());
                templatePool.add(TemplateLevel.GLOBAL.toString(), parseContext);
                return;
            }
            case CONCRETE: {
                String localId = ruleTemplate.getLocalId();
                FraudoParser.ParseContext parseContext = fraudContextParser.parse(ruleTemplate.getTemplate());
                templatePool.add(localId, parseContext);
                return;
            }
            default: {
                log.warn("This template lvl={} is not supported!", lvl);
            }
        }
    }
}
