package com.rbkmoney.fraudbusters.listener.payment;

import com.rbkmoney.damsel.fraudbusters.FraudPayment;
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
public class FraudPaymentListener {

    private final Repository<FraudPayment> repository;

    @KafkaListener(topics = "${kafka.topic.fraud.payment}", containerFactory = "fraudPaymentListenerContainerFactory")
    public void listen(List<FraudPayment> payments, @Header(KafkaHeaders.RECEIVED_PARTITION_ID) Integer partition,
                       @Header(KafkaHeaders.OFFSET) Long offset) throws InterruptedException {
        try {
            log.info("FraudPaymentListener listen result size: {} partition: {} offset: {}", payments.size(), partition, offset);
            repository.insertBatch(payments);
        } catch (Exception e) {
            log.warn("Error when FraudPaymentListener listen e: ", e);
            Thread.sleep(KafkaConfig.THROTTLING_TIMEOUT);
            throw e;
        }
    }
}
