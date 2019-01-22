package com.rbkmoney.fraudbusters.template;

import com.rbkmoney.fraudbusters.constant.TemplateLevel;
import com.rbkmoney.fraudbusters.domain.RuleTemplate;
import com.rbkmoney.fraudbusters.template.pool.StreamPool;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DeleteCommand implements TemplateCommandInterface {

    private final StreamPool pool;

    @Override
    public void execute(RuleTemplate ruleTemplate) {
        switch (ruleTemplate.getLvl()) {
            case GLOBAL:
                pool.stopAndRemove(TemplateLevel.GLOBAL.toString());
                break;
            case CONCRETE:
                pool.stopAndRemove(ruleTemplate.getLocalId());
                break;
        }
    }
}
