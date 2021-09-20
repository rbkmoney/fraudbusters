package com.rbkmoney.fraudbusters.listener.payment.historical;

import com.rbkmoney.damsel.fraudbusters.Command;
import com.rbkmoney.damsel.fraudbusters.Group;
import com.rbkmoney.damsel.fraudbusters.PriorityId;
import com.rbkmoney.fraudbusters.pool.HistoricalPool;
import com.rbkmoney.fraudbusters.util.TimestampUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class AbstractTimeGroupCommandListenerExecutor {

    protected void execCommand(Command command, HistoricalPool<List<String>> pool) {
        Group group = command.getCommandBody().getGroup();
        Long timestamp = TimestampUtil.parseInstantFromString(command.getCommandTime()).toEpochMilli();
        switch (command.command_type) {
            case CREATE -> createGroup(group, timestamp, pool);
            case DELETE -> pool.add(group.getGroupId(), timestamp, null);
            default -> log.error("Unknown command: {}", command);
        }
    }

    private void createGroup(Group group, Long time, HistoricalPool<List<String>> pool) {
        List<String> sortedListTemplates = group.template_ids.stream()
                .sorted(Comparator.comparing(PriorityId::getPriority))
                .map(PriorityId::getId)
                .collect(Collectors.toList());
        pool.add(group.getGroupId(), time, sortedListTemplates);
    }

}
