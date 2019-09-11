package com.rbkmoney.fraudbusters.listener;

import com.rbkmoney.damsel.fraudbusters.Command;
import com.rbkmoney.damsel.fraudbusters.TemplateReference;
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
public class TemplateReferenceListener implements CommandListener {

    private final Pool<String> referencePoolImpl;

    @Override
    @KafkaListener(topics = "${kafka.topic.reference}", containerFactory = "referenceListenerContainerFactory")
    public void listen(@Payload Command command) {
        log.info("TemplateReferenceListener command: {}", command);
        if (command != null && command.isSetCommandBody() && command.getCommandBody().isSetReference()) {
            TemplateReference reference = command.getCommandBody().getReference();
            String key = ReferenceKeyGenerator.generateTemplateKey(reference);
            execCommand(command, key);
        }
    }

    private void execCommand(Command command, String key) {
        switch (command.command_type) {
            case CREATE:
                referencePoolImpl.add(key, command.getCommandBody().getReference().template_id);
                return;
            case DELETE:
                referencePoolImpl.remove(key);
                return;
            default:
                log.error("Unknown command: {}", command);
        }
    }
}
