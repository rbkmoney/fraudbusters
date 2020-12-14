package com.rbkmoney.fraudbusters.service;

import com.rbkmoney.damsel.fraudbusters.ReferenceInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class InitiatingEntitySourceService {

    private final KafkaTemplate<String, ReferenceInfo> kafkaUnknownInitiatingEntityTemplate;

    public void sendToSource(ReferenceInfo referenceInfo) {
        try {
            log.info("Got a new entity ('{}'). We should try to create a default template.", referenceInfo);
            kafkaUnknownInitiatingEntityTemplate.send("", referenceInfo);
            log.info("New entity with successfully send to source entity ('{}')", referenceInfo);
        } catch (Exception e) {
            log.error("Could not interact with fraudbusters-management", e);
        }
    }
}
