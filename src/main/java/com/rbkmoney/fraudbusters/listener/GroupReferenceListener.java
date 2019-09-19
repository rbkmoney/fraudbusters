package com.rbkmoney.fraudbusters.listener;

import com.rbkmoney.damsel.fraudbusters.Command;
import com.rbkmoney.damsel.fraudbusters.GroupReference;
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
public class GroupReferenceListener implements CommandListener {

    private final Pool<String> groupReferencePoolImpl;

    @Override
    @KafkaListener(topics = "${kafka.topic.group.reference}", containerFactory = "groupReferenceListenerContainerFactory")
    public void listen(@Payload Command command) {
        log.info("GroupReferenceListener command: {}", command);
        if (command != null && command.isSetCommandBody() && command.getCommandBody().isSetGroupReference()) {
            GroupReference reference = command.getCommandBody().getGroupReference();
            String key = ReferenceKeyGenerator.generateTemplateKey(reference.getPartyId(), reference.getShopId());
            execCommand(command, key);
        }
    }

    private void execCommand(Command command, String key) {
        switch (command.command_type) {
            case CREATE:
                groupReferencePoolImpl.add(key, command.getCommandBody().getGroupReference().getGroupId());
                return;
            case DELETE:
                groupReferencePoolImpl.remove(key);
                return;
            default:
                log.error("Unknown command: {}", command);
        }
    }
}
