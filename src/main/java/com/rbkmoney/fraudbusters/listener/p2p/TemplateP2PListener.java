package com.rbkmoney.fraudbusters.listener.p2p;

import com.rbkmoney.damsel.fraudbusters.Command;
import com.rbkmoney.damsel.fraudbusters.Template;
import com.rbkmoney.fraudbusters.fraud.FraudContextParser;
import com.rbkmoney.fraudbusters.listener.AbstractPoolCommandListenerExecutor;
import com.rbkmoney.fraudbusters.listener.CommandListener;
import com.rbkmoney.fraudbusters.template.pool.Pool;
import com.rbkmoney.fraudo.FraudoP2PParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.antlr.v4.runtime.ParserRuleContext;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;


@Slf4j
@Component
@RequiredArgsConstructor
public class TemplateP2PListener extends AbstractPoolCommandListenerExecutor implements CommandListener {

    private final FraudContextParser<FraudoP2PParser.ParseContext> p2pContextParser;
    private final Pool<ParserRuleContext> templateP2PPoolImpl;

    @Override
    @KafkaListener(topics = "${kafka.topic.p2p.template}", containerFactory = "templateP2PListenerContainerFactory")
    public void listen(@Payload Command command) {
        log.info("TemplateP2PListener command: {}", command);
        if (command != null && command.isSetCommandBody() && command.getCommandBody().isSetTemplate()) {
            Template template = command.getCommandBody().getTemplate();
            String templateString = new String(template.getTemplate(), StandardCharsets.UTF_8);
            log.info("TemplateP2PListener templateString: {}", templateString);
            execCommand(command, template.getId(), templateP2PPoolImpl, p2pContextParser::parse, templateString);
        }
    }

}
