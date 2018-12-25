package com.rbkmoney.fraudbusters.template;

import com.rbkmoney.fraudbusters.domain.RuleTemplate;

public interface TemplateDispatcher {

    void doDispatch(RuleTemplate ruleTemplate);

}
