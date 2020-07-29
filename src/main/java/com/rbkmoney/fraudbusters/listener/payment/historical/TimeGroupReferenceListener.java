package com.rbkmoney.fraudbusters.listener.payment.historical;

import com.rbkmoney.damsel.fraudbusters.Command;
import com.rbkmoney.damsel.fraudbusters.GroupReference;
import com.rbkmoney.fraudbusters.listener.CommandListener;
import com.rbkmoney.fraudbusters.pool.HistoricalPool;
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
public class TimeGroupReferenceListener extends AbstractTimePoolCommandListenerExecutor implements CommandListener {

    private final HistoricalPool<String> timeGroupReferencePoolImpl;

    @Override
    @KafkaListener(topics = "${kafka.topic.full-group-reference}", containerFactory = "timeGroupReferenceListenerContainerFactory")
    public void listen(@Payload Command command) {
        log.info("TimeGroupReferenceListener command: {}", command);
        if (command != null && command.isSetCommandBody() && command.getCommandBody().isSetGroupReference()) {
            GroupReference reference = command.getCommandBody().getGroupReference();
            String key = ReferenceKeyGenerator.generateTemplateKeyByList(reference.getPartyId(), reference.getShopId());
            GroupReference groupReference = command.getCommandBody().getGroupReference();
            Long timestamp = TimestampUtil.parseInstantFromString(command.getCommandTime()).toEpochMilli();
            execCommand(command, key, timestamp, timeGroupReferencePoolImpl, groupReference::getGroupId);
        }
    }

}
