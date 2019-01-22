package com.rbkmoney.fraudbusters.template;

import com.rbkmoney.fraudbusters.constant.TemplateLevel;
import com.rbkmoney.fraudbusters.domain.RuleTemplate;
import com.rbkmoney.fraudbusters.template.pool.StreamPool;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class DeleteCommand implements TemplateCommandInterface {

    private final StreamPool pool;

    @Override
    public void execute(RuleTemplate ruleTemplate) {
        TemplateLevel lvl = ruleTemplate.getLvl();
        switch (lvl) {
            case GLOBAL:
                pool.stopAndRemove(TemplateLevel.GLOBAL.toString());
                break;
            case CONCRETE:
                pool.stopAndRemove(ruleTemplate.getLocalId());
                break;
            default: {
                log.warn("This template lvl={} is not supported!", lvl);
            }
        }
    }
}
