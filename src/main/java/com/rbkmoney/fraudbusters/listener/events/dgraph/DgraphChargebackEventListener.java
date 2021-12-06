package com.rbkmoney.fraudbusters.listener.events.dgraph;

import com.rbkmoney.damsel.fraudbusters.Chargeback;
import com.rbkmoney.fraudbusters.domain.dgraph.common.DgraphChargeback;
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
public class DgraphChargebackEventListener {

    private final Repository<DgraphChargeback> repository;
    private final Converter<Chargeback, DgraphChargeback> chargebackToDgraphChargebackConverter;

    @KafkaListener(topics = "${kafka.dgraph.topics.chargeback.name}",
            containerFactory = "kafkaDgraphChargebackResultListenerContainerFactory")
    public void listen(List<ConsumerRecord<String, Chargeback>> records,
                       Acknowledgment ack) throws InterruptedException {
        ConsumerRecord<String, Chargeback> firstRecord = records.stream()
                .findFirst()
                .orElseThrow(() -> new NotFoundException("First chargeback in a batch was not found!"));
        log.info("DgraphChargebackEventListener listen result size: {} partition: {} offset: {}",
                records.size(), firstRecord.partition(), firstRecord.offset());
        for (ConsumerRecord<String, Chargeback> record : records) {
            DgraphChargeback dgraphChargeback = chargebackToDgraphChargebackConverter.convert(record.value());
            repository.insert(dgraphChargeback);
        }
        ack.acknowledge();
    }
}
