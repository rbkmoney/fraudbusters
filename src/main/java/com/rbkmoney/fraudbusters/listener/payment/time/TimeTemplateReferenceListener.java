package com.rbkmoney.fraudbusters.listener.payment.time;

import com.rbkmoney.damsel.fraudbusters.Command;
import com.rbkmoney.damsel.fraudbusters.TemplateReference;
import com.rbkmoney.fraudbusters.listener.CommandListener;
import com.rbkmoney.fraudbusters.template.pool.TimePool;
import com.rbkmoney.fraudbusters.util.ReferenceKeyGenerator;
import com.rbkmoney.fraudbusters.util.TimestampUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class TimeTemplateReferenceListener extends AbstractTimePoolCommandListenerExecutor implements CommandListener {

    private final TimePool<String> timeReferencePoolImpl;

    @Override
    @KafkaListener(topics = "${kafka.topic.full-reference}", containerFactory = "timeReferenceListenerContainerFactory")
    public void listen(@Payload Command command) {
        log.info("TimeTemplateReferenceListener command: {}", command);
        if (command != null && command.isSetCommandBody() && command.getCommandBody().isSetReference()) {
            TemplateReference reference = command.getCommandBody().getReference();
            String key = ReferenceKeyGenerator.generateTemplateKey(reference);
            TemplateReference templateReference = command.getCommandBody().getReference();
            Long timestamp = TimestampUtil.parseInstantFromString(command.getCommandTime()).toEpochMilli();
            execCommand(command, key, timestamp, timeReferencePoolImpl, templateReference::getTemplateId);
        }
    }

}
