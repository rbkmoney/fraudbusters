package com.rbkmoney.fraudbusters.listener.payment;

import com.rbkmoney.damsel.fraudbusters.Refund;
import com.rbkmoney.fraudbusters.config.KafkaConfig;
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
public class RefundEventListener {

    private final Repository<Refund> repository;

    @KafkaListener(topics = "${kafka.topic.event.sink.refund}", containerFactory = "kafkaListenerContainerFactory")
    public void listen(List<Refund> refunds, @Header(KafkaHeaders.RECEIVED_PARTITION_ID) Integer partition,
                       @Header(KafkaHeaders.OFFSET) Long offset) throws InterruptedException {
        try {
            log.info("RefundEventListener listen result size: {} partition: {} offset: {}", refunds.size(), partition, offset);
            repository.insertBatch(refunds);
        } catch (Exception e) {
            log.warn("Error when RefundEventListener listen e: ", e);
            Thread.sleep(KafkaConfig.THROTTLING_TIMEOUT);
            throw e;
        }
    }
}
