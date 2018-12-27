package com.rbkmoney.fraudbusters.listener;

import com.rbkmoney.fraudbusters.domain.FraudResult;
import com.rbkmoney.fraudbusters.repository.FraudResultRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ResultAggregatorListener {

    private final FraudResultRepository fraudResultRepository;

    @KafkaListener(topics = "${kafka.result.stream.topic}", containerFactory = "resultListenerContainerFactory")
    public void listen(List<FraudResult> batch) {
        log.info("ResultAggregatorListener listen result: {}", batch);
        fraudResultRepository.insertBatch(batch);
    }
}
