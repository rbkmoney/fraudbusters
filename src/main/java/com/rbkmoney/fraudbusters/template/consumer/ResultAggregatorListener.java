package com.rbkmoney.fraudbusters.template.consumer;

import com.rbkmoney.fraudbusters.domain.FraudResult;
import com.rbkmoney.fraudbusters.repository.CountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ResultAggregatorListener {

    private final CountRepository countRepository;

    @KafkaListener(topics = "${kafka.result.stream.topic}", containerFactory = "resultListenerContainerFactory")
    public void listen(FraudResult result) {
        log.info("ResultAggregatorListener listen result: {}", result);
        countRepository.create(result.getFraudModel());
    }
}
