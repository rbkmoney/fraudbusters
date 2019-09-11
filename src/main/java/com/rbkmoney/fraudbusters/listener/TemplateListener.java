package com.rbkmoney.fraudbusters.listener;

import com.rbkmoney.damsel.fraudbusters.Command;
import com.rbkmoney.damsel.fraudbusters.Template;
import com.rbkmoney.fraudbusters.fraud.FraudContextParser;
import com.rbkmoney.fraudbusters.template.pool.Pool;
import com.rbkmoney.fraudo.FraudoParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;


@Slf4j
@Component
@RequiredArgsConstructor
public class TemplateListener implements CommandListener {

    private final FraudContextParser fraudContextParser;
    private final Pool<FraudoParser.ParseContext> templatePoolImpl;

    @Override
    @KafkaListener(topics = "${kafka.topic.template}", containerFactory = "templateListenerContainerFactory")
    public void listen(@Payload Command command) {
        log.info("TemplateListener command: {}", command);
        if (command != null && command.isSetCommandBody() && command.getCommandBody().isSetTemplate()) {
            Template template = command.getCommandBody().getTemplate();
            String templateString = new String(template.getTemplate(), StandardCharsets.UTF_8);
            FraudoParser.ParseContext parseContext = fraudContextParser.parse(templateString);
            execCommand(command, parseContext);
        }
    }

    private void execCommand(Command command, FraudoParser.ParseContext context) {
        Template template = command.getCommandBody().getTemplate();
        switch (command.command_type) {
            case CREATE:
                templatePoolImpl.add(template.getId(), context);
                return;
            case DELETE:
                templatePoolImpl.remove(template.getId());
                return;
            default:
                log.error("Unknown command: {}", command);
        }
    }
}
