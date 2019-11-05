package com.rbkmoney.fraudbusters.listener.p2p;

import com.rbkmoney.damsel.fraudbusters.Command;
import com.rbkmoney.damsel.fraudbusters.TemplateReference;
import com.rbkmoney.fraudbusters.listener.AbstractPoolCommandListenerExecutor;
import com.rbkmoney.fraudbusters.listener.CommandListener;
import com.rbkmoney.fraudbusters.template.pool.Pool;
import com.rbkmoney.fraudbusters.util.ReferenceKeyGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class TemplateP2PReferenceListener extends AbstractPoolCommandListenerExecutor implements CommandListener {

    private final Pool<String> referencePoolImpl;

    @Override
    @KafkaListener(topics = "${kafka.topic.p2p.reference}", containerFactory = "referenceListenerContainerFactory")
    public void listen(@Payload Command command) {
        log.info("TemplateReferenceListener command: {}", command);
        if (command != null && command.isSetCommandBody() && command.getCommandBody().isSetReference()) {
            TemplateReference reference = command.getCommandBody().getReference();
            String key = ReferenceKeyGenerator.generateTemplateKey(reference);
            TemplateReference templateReference = command.getCommandBody().getReference();
            execCommand(command, key, referencePoolImpl, templateReference::getTemplateId);
        }
    }

}
