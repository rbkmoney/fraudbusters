package com.rbkmoney.fraudbusters.listener.p2p;

import com.rbkmoney.damsel.fraudbusters.Command;
import com.rbkmoney.damsel.fraudbusters.P2PReference;
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
public class TemplateP2PReferenceListener extends AbstractPoolCommandListenerExecutor implements CommandListener {

    private final Pool<String> referenceP2PPoolImpl;

    @Override
    @KafkaListener(topics = "${kafka.topic.p2p-reference}", containerFactory = "referenceP2PListenerContainerFactory")
    public void listen(@Payload Command command) {
        log.info("TemplateP2PReferenceListener command: {}", command);
        if (command != null && command.isSetCommandBody() && command.getCommandBody().isSetP2pReference()) {
            P2PReference p2pReference = command.getCommandBody().getP2pReference();
            String key = ReferenceKeyGenerator.generateP2PTemplateKey(p2pReference);
            execCommand(command, key, referenceP2PPoolImpl, p2pReference::getTemplateId);
        }
    }

}
