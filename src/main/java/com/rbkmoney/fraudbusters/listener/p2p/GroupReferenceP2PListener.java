package com.rbkmoney.fraudbusters.listener.p2p;

import com.rbkmoney.damsel.fraudbusters.Command;
import com.rbkmoney.damsel.fraudbusters.P2PGroupReference;
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
public class GroupReferenceP2PListener extends AbstractPoolCommandListenerExecutor implements CommandListener {

    private final Pool<String> groupReferenceP2PPoolImpl;

    @Override
    @KafkaListener(topics = "${kafka.topic.p2p.group.reference}", containerFactory = "groupReferenceP2PListenerContainerFactory")
    public void listen(@Payload Command command) {
        log.info("GroupP2PReferenceListener command: {}", command);
        if (command != null && command.isSetCommandBody() && command.getCommandBody().isSetP2pGroupReference()) {
            P2PGroupReference p2pGroupReference = command.getCommandBody().getP2pGroupReference();
            String key = ReferenceKeyGenerator.generateTemplateKeyByList(p2pGroupReference.getIdentityId());
            execCommand(command, key, groupReferenceP2PPoolImpl, p2pGroupReference::getGroupId);
        }
    }

}
