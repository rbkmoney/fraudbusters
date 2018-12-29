package com.rbkmoney.fraudbusters;

import com.rbkmoney.fraudbusters.listener.TemplateListener;
import com.rbkmoney.fraudbusters.template.pool.StreamPool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;

import javax.annotation.PreDestroy;

@ServletComponentScan
@SpringBootApplication
public class FraudBustersApplication {

    @Autowired
    private StreamPool pool;

    @Autowired
    private ReplyingKafkaTemplate replyingKafkaTemplate;

    @Autowired
    private KafkaListenerEndpointRegistry registry;

    public static void main(String[] args) {
        SpringApplication.run(FraudBustersApplication.class, args);
    }

    @PreDestroy
    public void preDestroy() {
        registry.stop();
        replyingKafkaTemplate.stop();
        pool.clear();
    }
}
