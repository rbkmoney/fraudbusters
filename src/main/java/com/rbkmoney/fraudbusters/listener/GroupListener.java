package com.rbkmoney.fraudbusters.listener;

import com.rbkmoney.damsel.fraudbusters.Command;
import com.rbkmoney.damsel.fraudbusters.Group;
import com.rbkmoney.damsel.fraudbusters.PriorityId;
import com.rbkmoney.fraudbusters.template.pool.Pool;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;


@Slf4j
@Component
@RequiredArgsConstructor
public class GroupListener implements CommandListener {

    private final Pool<List<String>> groupPoolImpl;

    @Override
    @KafkaListener(topics = "${kafka.topic.group.list}", containerFactory = "groupListenerContainerFactory")
    public void listen(@Payload Command command) {
        log.info("GroupListener command: {}", command);
        if (command != null && command.isSetCommandBody() && command.getCommandBody().isSetGroup()) {
            execCommand(command);
        }
    }

    private void execCommand(Command command) {
        Group group = command.getCommandBody().getGroup();
        switch (command.command_type) {
            case CREATE:
                createGroup(group);
                return;
            case DELETE:
                groupPoolImpl.remove(group.getGroupId());
                return;
            default:
                log.error("Unknown command: {}", command);
        }
    }

    private void createGroup(Group group) {
        List<String> sortedListTemplates = group.template_ids.stream()
                .sorted(Comparator.comparing(PriorityId::getPriority))
                .map(PriorityId::getId)
                .collect(Collectors.toList());
        groupPoolImpl.add(group.getGroupId(), sortedListTemplates);
    }

}
