package com.rbkmoney.fraudbusters.listener.events.dgraph;

import com.rbkmoney.damsel.fraudbusters.FraudPayment;
import com.rbkmoney.fraudbusters.domain.dgraph.common.DgraphFraudPayment;
import com.rbkmoney.fraudbusters.exception.NotFoundException;
import com.rbkmoney.fraudbusters.repository.Repository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.core.convert.converter.Converter;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class DgraphFraudPaymentListener {

    private final Repository<DgraphFraudPayment> repository;
    private final Converter<FraudPayment, DgraphFraudPayment> fraudPaymentToDgraphFraudPaymentConverter;

    @KafkaListener(topics = "${kafka.dgraph.topics.fraud_payment.name}",
            containerFactory = "kafkaDrgaphFraudPaymentListenerContainerFactory")
    public void listen(List<ConsumerRecord<String, FraudPayment>> records,
                       Acknowledgment ack) throws InterruptedException {
        ConsumerRecord<String, FraudPayment> firstRecord = records.stream()
                .findFirst()
                .orElseThrow(() -> new NotFoundException("First fraud payment in a batch was not found!"));
        log.info("DgraphFraudPaymentListener listen result size: {} partition: {} offset: {}",
                records.size(), firstRecord.partition(), firstRecord.offset());
        for (ConsumerRecord<String, FraudPayment> record : records) {
            DgraphFraudPayment dgraphFraudPayment = fraudPaymentToDgraphFraudPaymentConverter.convert(record.value());
            repository.insert(dgraphFraudPayment);
        }
        ack.acknowledge();
    }

}
