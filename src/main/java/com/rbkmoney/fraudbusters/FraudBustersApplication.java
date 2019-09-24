package com.rbkmoney.fraudbusters;

import com.rbkmoney.fraudbusters.domain.FraudRequest;
import com.rbkmoney.fraudbusters.domain.FraudResult;
import com.rbkmoney.fraudbusters.listener.StartupListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;

import javax.annotation.PreDestroy;

@Slf4j
@ServletComponentScan
@SpringBootApplication
public class FraudBustersApplication extends SpringApplication {

    @Autowired
    private ReplyingKafkaTemplate replyingKafkaTemplate;

    @Autowired
    private KafkaListenerEndpointRegistry registry;

    @Autowired
    private StartupListener startupListener;

    @Autowired
    private ReplyingKafkaTemplate<String, FraudRequest, FraudResult> replyKafkaTemplate;

    public static void main(String[] args) {
        SpringApplication.run(FraudBustersApplication.class, args);
    }

    @PreDestroy
    public void preDestroy() {
        log.info("FraudBustersApplication preDestroy!");
        registry.stop();
        replyKafkaTemplate.stop();
        replyingKafkaTemplate.stop();
        startupListener.stop();
    }
}
