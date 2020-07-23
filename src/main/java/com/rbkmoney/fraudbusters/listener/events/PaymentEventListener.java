package com.rbkmoney.fraudbusters.listener.events;

import com.rbkmoney.damsel.fraudbusters.Payment;
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
public class PaymentEventListener {

    private final Repository<Payment> repository;

    @KafkaListener(topics = "${kafka.topic.event.sink.payment}", containerFactory = "kafkaPaymentResultListenerContainerFactory")
    public void listen(List<Payment> payments, @Header(KafkaHeaders.RECEIVED_PARTITION_ID) Integer partition,
                       @Header(KafkaHeaders.OFFSET) Long offset) throws InterruptedException {
        try {
            log.info("PaymentEventListener listen result size: {} partition: {} offset: {}", payments.size(), partition, offset);
            repository.insertBatch(payments);
        } catch (Exception e) {
            log.warn("Error when PaymentEventListener listen e: ", e);
            Thread.sleep(KafkaConfig.THROTTLING_TIMEOUT);
            throw e;
        }
    }
}
