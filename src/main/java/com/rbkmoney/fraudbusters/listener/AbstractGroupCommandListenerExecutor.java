package com.rbkmoney.fraudbusters.listener;

import com.rbkmoney.damsel.fraudbusters.Command;
import com.rbkmoney.damsel.fraudbusters.Group;
import com.rbkmoney.damsel.fraudbusters.PriorityId;
import com.rbkmoney.fraudbusters.pool.Pool;
import lombok.extern.slf4j.Slf4j;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class AbstractGroupCommandListenerExecutor {

    protected void execCommand(Command command, Pool<List<String>> pool) {
        Group group = command.getCommandBody().getGroup();
        switch (command.command_type) {
            case CREATE:
                createGroup(group, pool);
                return;
            case DELETE:
                pool.remove(group.getGroupId());
                return;
            default:
                log.error("Unknown command: {}", command);
        }
    }

    private void createGroup(Group group, Pool<List<String>> pool) {
        List<String> sortedListTemplates = group.template_ids.stream()
                .sorted(Comparator.comparing(PriorityId::getPriority))
                .map(PriorityId::getId)
                .collect(Collectors.toList());
        pool.add(group.getGroupId(), sortedListTemplates);
    }

}
