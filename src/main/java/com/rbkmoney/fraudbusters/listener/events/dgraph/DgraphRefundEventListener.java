package com.rbkmoney.fraudbusters.listener.events.dgraph;

import com.rbkmoney.damsel.fraudbusters.Refund;
import com.rbkmoney.fraudbusters.domain.dgraph.DgraphRefund;
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
public class DgraphRefundEventListener {

    private final Repository<DgraphRefund> repository;
    private final Converter<Refund, DgraphRefund>  converter;

    @KafkaListener(topics = "${kafka.dgraph.topics.refund.name}",
            containerFactory = "kafkaDgraphRefundResultListenerContainerFactory")
    public void listen(List<ConsumerRecord<String, Refund>> records,
                       Acknowledgment ack) throws InterruptedException {
        final ConsumerRecord<String, Refund> firstRecord = records.stream()
                .findFirst()
                .orElseThrow(() -> new NotFoundException("First fraud payment in a batch was not found!"));
        log.info("DgraphRefundEventListener listen result size: {} partition: {} offset: {}",
                records.size(), firstRecord.partition(), firstRecord.offset());
        for (ConsumerRecord<String, Refund> record : records) {
            Refund refund = record.value();
            repository.insert(converter.convert(refund));
        }

        ack.acknowledge();

    }
}
