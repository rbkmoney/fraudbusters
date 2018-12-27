package com.rbkmoney.fraudbusters.listener;

import com.rbkmoney.fraudbusters.domain.RuleTemplate;
import com.rbkmoney.fraudbusters.template.TemplateDispatcherImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class TemplateListener {

    private final TemplateDispatcherImpl templateDispatcherImpl;

    @KafkaListener(topics = "${kafka.template.topic}", containerFactory = "templateListenerContainerFactory")
    public void listen(RuleTemplate ruleTemplate) {
        log.info("TemplateListener ruleTemplate: {}", ruleTemplate);
        templateDispatcherImpl.doDispatch(ruleTemplate);
    }
}
