package com.rbkmoney.fraudbusters.listener.payment.historical;

import com.rbkmoney.damsel.fraudbusters.Command;
import com.rbkmoney.fraudbusters.listener.CommandListener;
import com.rbkmoney.fraudbusters.pool.HistoricalPool;
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

    private final HistoricalPool<List<String>> timeGroupPoolImpl;

    @Override
    @KafkaListener(autoStartup = "${kafka.historical.listener.enable}", topics = "${kafka.topic.full-group-list}",
            containerFactory = "timeGroupListenerContainerFactory")
    public void listen(@Payload Command command) {
        log.info("TimeGroupListener command: {}", command);
        if (command != null && command.isSetCommandBody() && command.getCommandBody().isSetGroup()) {
            execCommand(command, timeGroupPoolImpl);
        }
    }

}
