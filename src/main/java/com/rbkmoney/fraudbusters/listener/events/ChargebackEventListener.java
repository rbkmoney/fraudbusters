package com.rbkmoney.fraudbusters.listener.events;

import com.rbkmoney.damsel.fraudbusters.Chargeback;
import com.rbkmoney.fraudbusters.config.service.ListenersConfigurationService;
import com.rbkmoney.fraudbusters.repository.Repository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ChargebackEventListener {

    private final Repository<Chargeback> repository;

    @KafkaListener(topics = "${kafka.topic.event.sink.chargeback}",
            containerFactory = "kafkaChargebackResultListenerContainerFactory")
    public void listen(
            List<Chargeback> chargeback, @Header(KafkaHeaders.RECEIVED_PARTITION_ID) Integer partition,
            @Header(KafkaHeaders.OFFSET) Long offset) throws InterruptedException {
        try {
            log.info(
                    "ChargebackEventListener listen result size: {} partition: {} offset: {}",
                    chargeback.size(),
                    partition,
                    offset
            );
            repository.insertBatch(chargeback);
        } catch (Exception e) {
            log.warn("Error when ChargebackEventListener listen e: ", e);
            Thread.sleep(ListenersConfigurationService.THROTTLING_TIMEOUT);
            throw e;
        }
    }
}
