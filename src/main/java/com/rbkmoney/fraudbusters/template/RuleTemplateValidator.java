package com.rbkmoney.fraudbusters.template;

import com.rbkmoney.fraudbusters.domain.RuleTemplate;

public class RuleTemplateValidator {

    public static boolean validate(RuleTemplate ruleTemplate) {
        return ruleTemplate.getCommandType() != null && ruleTemplate.getLvl() != null;
    }

}
