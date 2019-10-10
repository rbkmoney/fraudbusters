package com.rbkmoney.fraudbusters.listener;

import com.rbkmoney.fraudbusters.converter.FraudResultToEventConverter;
import com.rbkmoney.fraudbusters.domain.FraudResult;
import com.rbkmoney.fraudbusters.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ResultAggregatorListener {

    private final EventRepository eventRepository;
    private final FraudResultToEventConverter fraudResultToEventConverter;

    @KafkaListener(topics = "${kafka.topic.result}", containerFactory = "kafkaListenerContainerFactory")
    public void listen(List<FraudResult> batch) {
        log.info("ResultAggregatorListener listen result: {}", batch);
        eventRepository.insertBatch(fraudResultToEventConverter.convertBatch(batch));
    }
}
