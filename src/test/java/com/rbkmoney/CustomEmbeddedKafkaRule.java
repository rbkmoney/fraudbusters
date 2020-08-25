package com.rbkmoney;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.test.rule.EmbeddedKafkaRule;

import java.io.IOException;
import java.net.ServerSocket;

@Slf4j
public class CustomEmbeddedKafkaRule extends EmbeddedKafkaRule {
    public CustomEmbeddedKafkaRule(int count) {
        super(count);
        try {
            int localPort = new ServerSocket(0).getLocalPort();
            log.info("Free port for broker: {}", localPort);
            super.getEmbeddedKafka().kafkaPorts(localPort);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public CustomEmbeddedKafkaRule(int count, boolean controlledShutdown, String... topics) {
        super(count, controlledShutdown, topics);
        try {
            int localPort = new ServerSocket(0).getLocalPort();
            log.info("Free port for broker: {}", localPort);
            super.getEmbeddedKafka().kafkaPorts(localPort);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public CustomEmbeddedKafkaRule(int count, boolean controlledShutdown, int partitions, String... topics) {
        super(count, controlledShutdown, partitions, topics);
        try {
            int localPort = new ServerSocket(0).getLocalPort();
            log.info("Free port for broker: {}", localPort);
            super.getEmbeddedKafka().kafkaPorts(localPort);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
