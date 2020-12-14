package com.rbkmoney.fraudbusters.service;

import com.rbkmoney.damsel.fraudbusters.ReferenceInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class InitiatingEntitySourceService {

    @Value("${kafka.topic.unknown-initiating-entity}")
    private String topic;

    private final KafkaTemplate<String, ReferenceInfo> kafkaUnknownInitiatingEntityTemplate;

    public void sendToSource(ReferenceInfo referenceInfo) {
        try {
            log.info("Got a new entity ('{}'). We should try to create a default template.", referenceInfo);
            kafkaUnknownInitiatingEntityTemplate.send(topic, referenceInfo);
            log.info("New entity with successfully send to source entity ('{}')", referenceInfo);
        } catch (Exception e) {
            log.error("Could send referenceInfo: {} to topic: {}", referenceInfo, topic, e);
        }
    }
}
