package com.rbkmoney.fraudbusters.listener;

import com.rbkmoney.fraudbusters.converter.FraudResultToEventConverter;
import com.rbkmoney.fraudbusters.domain.MgEventSinkRow;
import com.rbkmoney.fraudbusters.repository.MgEventSinkRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class MgEventSinkAggregatorListener {

    private final MgEventSinkRepository mgEventSinkRepository;

    @KafkaListener(topics = "${kafka.topic.event.sink.aggregated}", containerFactory = "mgEventSinkListenerContainerFactory")
    public void listen(List<MgEventSinkRow> batch) {
        log.info("MgEventSinkAggregatorListener listen result: {}", batch);
        mgEventSinkRepository.insertBatch(batch);
    }
}
