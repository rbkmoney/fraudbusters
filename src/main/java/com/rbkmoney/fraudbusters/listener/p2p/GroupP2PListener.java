package com.rbkmoney.fraudbusters.listener.p2p;

import com.rbkmoney.damsel.fraudbusters.Command;
import com.rbkmoney.fraudbusters.listener.AbstractGroupCommandListenerExecutor;
import com.rbkmoney.fraudbusters.listener.CommandListener;
import com.rbkmoney.fraudbusters.template.pool.Pool;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.List;


@Slf4j
@Component
@RequiredArgsConstructor
public class GroupP2PListener extends AbstractGroupCommandListenerExecutor implements CommandListener {

    private final Pool<List<String>> groupP2PPoolImpl;

    @Override
    @KafkaListener(topics = "${kafka.topic.p2p.group.list}", containerFactory = "groupP2PListenerContainerFactory")
    public void listen(@Payload Command command) {
        log.info("GroupP2PListener command: {}", command);
        if (command != null && command.isSetCommandBody() && command.getCommandBody().isSetGroup()) {
            execCommand(command, groupP2PPoolImpl);
        }
    }

}
