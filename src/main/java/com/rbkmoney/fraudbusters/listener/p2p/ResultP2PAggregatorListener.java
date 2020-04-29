package com.rbkmoney.fraudbusters.listener.p2p;

import com.rbkmoney.fraudbusters.config.KafkaConfig;
import com.rbkmoney.fraudbusters.converter.ScoresResultToEventConverter;
import com.rbkmoney.fraudbusters.domain.ScoresResult;
import com.rbkmoney.fraudbusters.fraud.model.P2PModel;
import com.rbkmoney.fraudbusters.repository.impl.EventP2PRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ResultP2PAggregatorListener {

    private final EventP2PRepository eventP2PRepository;
    private final ScoresResultToEventConverter scoresResultToEventConverter;

    @KafkaListener(topics = "${kafka.topic.p2p.result}", containerFactory = "kafkaListenerP2PResultContainerFactory")
    public void listen(List<ScoresResult<P2PModel>> batch) throws InterruptedException {
        try {
            log.info("ResultAggregatorListener listen result: {}", batch);
            eventP2PRepository.insertBatch(scoresResultToEventConverter.convertBatch(batch));
        } catch (Exception e) {
            log.warn("Error when ResultP2PAggregatorListener listen e: ", e);
            Thread.sleep(KafkaConfig.THROTTLING_TIMEOUT);
            throw e;
        }
    }

}
