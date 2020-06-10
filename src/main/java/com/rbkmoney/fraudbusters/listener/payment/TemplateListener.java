package com.rbkmoney.fraudbusters.listener.payment;

import com.rbkmoney.damsel.fraudbusters.Command;
import com.rbkmoney.damsel.fraudbusters.Template;
import com.rbkmoney.fraudbusters.fraud.FraudContextParser;
import com.rbkmoney.fraudbusters.listener.AbstractPoolCommandListenerExecutor;
import com.rbkmoney.fraudbusters.listener.CommandListener;
import com.rbkmoney.fraudbusters.template.pool.Pool;
import com.rbkmoney.fraudo.FraudoPaymentParser;
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
public class TemplateListener extends AbstractPoolCommandListenerExecutor implements CommandListener {

    private final FraudContextParser<FraudoPaymentParser.ParseContext> paymentContextParser;
    private final Pool<ParserRuleContext> templatePoolImpl;

    @Override
    @KafkaListener(topics = "${kafka.topic.template}", containerFactory = "templateListenerContainerFactory")
    public void listen(@Payload Command command) {
        log.info("TemplateListener command: {}", command);
        if (command != null && command.isSetCommandBody() && command.getCommandBody().isSetTemplate()) {
            Template template = command.getCommandBody().getTemplate();
            String templateString = new String(template.getTemplate(), StandardCharsets.UTF_8);
            execCommand(command, template.getId(), templatePoolImpl, paymentContextParser::parse, templateString);
        }
    }

}
