package com.rbkmoney.fraudbusters.listener.payment;

import com.rbkmoney.damsel.fraudbusters.Command;
import com.rbkmoney.damsel.fraudbusters.GroupReference;
import com.rbkmoney.fraudbusters.listener.AbstractPoolCommandListenerExecutor;
import com.rbkmoney.fraudbusters.listener.CommandListener;
import com.rbkmoney.fraudbusters.pool.Pool;
import com.rbkmoney.fraudbusters.util.ReferenceKeyGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class GroupReferenceListener extends AbstractPoolCommandListenerExecutor implements CommandListener {

    private final Pool<String> groupReferencePoolImpl;

    @Override
    @KafkaListener(topics = "${kafka.topic.group-reference}", containerFactory = "groupReferenceListenerContainerFactory")
    public void listen(@Payload Command command) {
        log.info("GroupReferenceListener command: {}", command);
        if (command != null && command.isSetCommandBody() && command.getCommandBody().isSetGroupReference()) {
            GroupReference reference = command.getCommandBody().getGroupReference();
            String key = ReferenceKeyGenerator.generateTemplateKeyByList(reference.getPartyId(), reference.getShopId());
            GroupReference groupReference = command.getCommandBody().getGroupReference();
            execCommand(command, key, groupReferencePoolImpl, groupReference::getGroupId);
        }
    }

}
