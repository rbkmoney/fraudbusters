package com.rbkmoney.fraudbusters.listener.payment;

import com.rbkmoney.fraudbusters.config.KafkaConfig;
import com.rbkmoney.fraudbusters.converter.FraudResultToEventConverter;
import com.rbkmoney.fraudbusters.domain.Event;
import com.rbkmoney.fraudbusters.domain.FraudResult;
import com.rbkmoney.fraudbusters.repository.Repository;
import com.rbkmoney.fraudbusters.service.FraudManagementService;
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
public class ResultAggregatorListener {

    private final Repository<Event> repository;
    private final FraudResultToEventConverter fraudResultToEventConverter;
    private final FraudManagementService fraudManagementService;

    @KafkaListener(topics = "${kafka.topic.result}", containerFactory = "kafkaListenerContainerFactory")
        public void listen(List<FraudResult> batch, @Header(KafkaHeaders.RECEIVED_PARTITION_ID) Integer partition,
                       @Header(KafkaHeaders.OFFSET) Long offset) throws InterruptedException {
        try {
            log.info("ResultAggregatorListener listen result size: {} partition: {} offset: {}", batch.size(), partition, offset);
            List<Event> events = fraudResultToEventConverter.convertBatch(batch);
            events.stream()
                    .filter(e -> fraudManagementService.isNewShop(e.getPartyId(), e.getShopId()))
                    .forEach(e -> fraudManagementService.createDefaultReference(e.getPartyId(), e.getShopId()));
            repository.insertBatch(events);
        } catch (Exception e) {
            log.warn("Error when ResultAggregatorListener listen e: ", e);
            Thread.sleep(KafkaConfig.THROTTLING_TIMEOUT);
            throw e;
        }
    }
}
