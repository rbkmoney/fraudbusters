package com.rbkmoney.fraudbusters.listener.payment.time;

import com.rbkmoney.damsel.fraudbusters.Command;
import com.rbkmoney.fraudbusters.listener.CommandListener;
import com.rbkmoney.fraudbusters.template.pool.time.TimePool;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.List;


@Slf4j
@Component
@RequiredArgsConstructor
public class TimeGroupListener extends AbstractTimeGroupCommandListenerExecutor implements CommandListener {

    private final TimePool<List<String>> timeGroupPoolImpl;

    @Override
    @KafkaListener(topics = "${kafka.topic.full.group.list}", containerFactory = "groupListenerContainerFactory")
    public void listen(@Payload Command command) {
        log.info("TimeGroupListener command: {}", command);
        if (command != null && command.isSetCommandBody() && command.getCommandBody().isSetGroup()) {
            execCommand(command, timeGroupPoolImpl);
        }
    }

}
