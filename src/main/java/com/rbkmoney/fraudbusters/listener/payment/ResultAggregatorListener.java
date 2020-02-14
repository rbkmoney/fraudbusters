package com.rbkmoney.fraudbusters.listener.payment;

import com.rbkmoney.fraudbusters.config.KafkaConfig;
import com.rbkmoney.fraudbusters.converter.FraudResultToEventConverter;
import com.rbkmoney.fraudbusters.domain.FraudResult;
import com.rbkmoney.fraudbusters.repository.EventRepository;
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

    private final EventRepository eventRepository;
    private final FraudResultToEventConverter fraudResultToEventConverter;

    @KafkaListener(topics = "${kafka.topic.result}", containerFactory = "kafkaListenerContainerFactory")
    public void listen(List<FraudResult> batch, @Header(KafkaHeaders.RECEIVED_PARTITION_ID) Integer partition,
                       @Header(KafkaHeaders.OFFSET) Long offset) throws InterruptedException {
        try {
            log.info("ResultAggregatorListener listen result size: {} partition: {} offset: {}", batch.size(), partition, offset);
            eventRepository.insertBatch(fraudResultToEventConverter.convertBatch(batch));
        } catch (Exception e) {
            log.warn("Error when ResultAggregatorListener listen e: ", e);
            Thread.sleep(KafkaConfig.THROTTLING_TIMEOUT);
        }
    }
}
