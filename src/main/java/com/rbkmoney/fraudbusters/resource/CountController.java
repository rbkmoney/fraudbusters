package com.rbkmoney.fraudbusters.resource;

import com.rbkmoney.fraudbusters.template.TemplateBroker;
import com.rbkmoney.fraudbusters.constant.Level;
import com.rbkmoney.fraudbusters.domain.RuleTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class CountController {

    @Autowired
    private TemplateBroker templateBroker;

    @GetMapping("/get")
    public Response get() {
        RuleTemplate ruleTemplate = new RuleTemplate();
        ruleTemplate.setLvl(Level.GLOBAL);
        ruleTemplate.setGlobalId("test");
        ruleTemplate.setTemplate("rule: 3 > 2 AND 1 = 1\n" +
                "-> accept;");
//        templateBroker.doDispatch(ruleTemplate);
        return null;
    }

}
