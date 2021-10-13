package com.rbkmoney.fraudbusters.listener.events.dgraph;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rbkmoney.damsel.fraudbusters.Payment;
import com.rbkmoney.damsel.fraudbusters.PaymentStatus;
import com.rbkmoney.fraudbusters.config.service.ListenersConfigurationService;
import com.rbkmoney.fraudbusters.converter.PaymentToDgraphPaymentConverter;
import com.rbkmoney.fraudbusters.converter.PaymentToPaymentModelConverter;
import com.rbkmoney.fraudbusters.domain.CheckedResultModel;
import com.rbkmoney.fraudbusters.domain.dgraph.DgraphPayment;
import com.rbkmoney.fraudbusters.exception.NotFoundException;
import com.rbkmoney.fraudbusters.repository.Repository;
import com.rbkmoney.fraudbusters.stream.impl.FullTemplateVisitorImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;

import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
public class DgraphPaymentEventListener {

    private final Repository<DgraphPayment> dgraphPaymentRepository;

    private final FullTemplateVisitorImpl fullTemplateVisitor;
    private final PaymentToPaymentModelConverter paymentToPaymentModelConverter;
    private final PaymentToDgraphPaymentConverter paymentToDgraphPaymentConverter;

    private final ObjectMapper objectMapper;

    @Value("${result.full.check.enabled:true}")
    private boolean isEnabledFullCheck;

    @KafkaListener(topics = "${kafka.dgraph.topics.payment.name}",
            containerFactory = "kafkaDgraphPaymentResultListenerContainerFactory")
    public void listen(List<ConsumerRecord<String, Payment>> records, Acknowledgment ack) throws InterruptedException {
        ConsumerRecord<String, Payment> firstRecord = records.stream()
                .findFirst()
                .orElseThrow(() -> new NotFoundException("First payment in a batch was not found!"));
        log.info("DgraphPaymentEventListener listen result size: {} partition: {} offset: {}",
                records.size(), firstRecord.partition(), firstRecord.offset());
        log.debug("DgraphPaymentEventListener listen result payments: {}", records);
        for (ConsumerRecord<String, Payment> record : records) {
            Payment payment = record.value();
            DgraphPayment dgraphPayment = paymentToDgraphPaymentConverter.convert(payment);
            fillAdditionalInfo(dgraphPayment, payment);
            dgraphPaymentRepository.insert(dgraphPayment);
        }
        ack.acknowledge();
    }

    private void fillAdditionalInfo(DgraphPayment dgraphPayment, Payment payment) {
        if (isEnabledFullCheck && PaymentStatus.processed.name().equals(dgraphPayment.getStatus())) {
            List<CheckedResultModel> listResults =
                    fullTemplateVisitor.visit(paymentToPaymentModelConverter.convert(payment));
            Optional<CheckedResultModel> first = listResults.stream()
                    .filter(checkedResultModel -> checkedResultModel.getCheckedTemplate() != null)
                    .findFirst();
            if (first.isPresent()) {
                CheckedResultModel checkedResultModel = first.get();
                dgraphPayment.setCheckedTemplate(checkedResultModel.getCheckedTemplate());
                dgraphPayment.setResultStatus(checkedResultModel.getResultModel().getResultStatus().name());
                dgraphPayment.setCheckedRule(checkedResultModel.getResultModel().getRuleChecked());
                try {
                    dgraphPayment.setCheckedResultsJson(objectMapper.writeValueAsString(listResults));
                } catch (JsonProcessingException e) {
                    log.warn("DgraphPaymentEventListener problem with serialize json! listResults: {}", listResults);
                }
            }
        }
    }

}
