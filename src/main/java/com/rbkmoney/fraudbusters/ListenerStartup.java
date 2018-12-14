package com.rbkmoney.fraudbusters;

import com.rbkmoney.fraudbusters.template.TemplateBroker;
import com.rbkmoney.fraudbusters.template.TemplateListener;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
@RequiredArgsConstructor
public class ListenerStartup implements ApplicationListener<ApplicationReadyEvent> {

    @Value("${kafka.bootstrap.servers}")
    private String bootstrapServers;

    @Value("${kafka.template.topic}")
    private String templateTopic;

    private final TemplateBroker templateBroker;

    private ExecutorService executorService = Executors.newSingleThreadExecutor();

    @Override
    public void onApplicationEvent(final ApplicationReadyEvent event) {
        executorService.submit(new TemplateListener(templateTopic, bootstrapServers, templateBroker));
        return;
    }
}
