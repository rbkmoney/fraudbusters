package com.rbkmoney.fraudbusters.listener;

import com.rbkmoney.fraudbusters.stream.TemplateStreamFactoryImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.streams.KafkaStreams;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Properties;

@Slf4j
@Component
@RequiredArgsConstructor
public class StartupListener implements ApplicationListener<ContextRefreshedEvent> {

    private final TemplateStreamFactoryImpl templateStreamFactoryImpl;
    private final Properties fraudStreamProperties;

    private KafkaStreams kafkaStreams;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        kafkaStreams = templateStreamFactoryImpl.create(fraudStreamProperties);
        log.info("StartupListener start stream kafkaStreams: ", kafkaStreams.allMetadata());
    }

    public void stop() {
        kafkaStreams.close(Duration.ofSeconds(10L));
    }

}