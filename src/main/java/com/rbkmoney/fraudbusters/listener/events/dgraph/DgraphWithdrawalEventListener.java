package com.rbkmoney.fraudbusters.listener.events.dgraph;

import com.rbkmoney.damsel.fraudbusters.Withdrawal;
import com.rbkmoney.fraudbusters.domain.dgraph.common.DgraphWithdrawal;
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
public class DgraphWithdrawalEventListener {

    private final Repository<DgraphWithdrawal> repository;
    private final Converter<Withdrawal, DgraphWithdrawal> converter;

    @KafkaListener(topics = "${kafka.dgraph.topics.withdrawal.name}",
            containerFactory = "kafkaDgraphWithdrawalResultListenerContainerFactory")
    public void listen(List<ConsumerRecord<String, Withdrawal>> records,
                       Acknowledgment ack) throws InterruptedException {
        ConsumerRecord<String, Withdrawal> firstRecord = records.stream()
                .findFirst()
                .orElseThrow(() -> new NotFoundException("First withdrawal in a batch was not found!"));
        log.info("DgraphWithdrawalEventListener listen result size: {} partition: {} offset: {}",
                records.size(), firstRecord.partition(), firstRecord.offset());
        log.debug("Listen withdrawals: {}", records);
        for (ConsumerRecord<String, Withdrawal> record : records) {
            repository.insert(converter.convert(record.value()));
        }
        ack.acknowledge();
    }

}
