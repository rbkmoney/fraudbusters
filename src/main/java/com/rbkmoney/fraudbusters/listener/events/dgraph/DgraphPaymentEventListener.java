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
import com.rbkmoney.fraudbusters.repository.Repository;
import com.rbkmoney.fraudbusters.stream.impl.FullTemplateVisitorImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class DgraphPaymentEventListener {

    private final Repository<DgraphPayment> dgraphPaymentRepository;

    private final FullTemplateVisitorImpl fullTemplateVisitor;
    private final PaymentToPaymentModelConverter paymentToPaymentModelConverter;
    private final PaymentToDgraphPaymentConverter paymentToDgraphPaymentConverter;

    private final ObjectMapper objectMapper;

    @Value("${result.full.check.enabled:true}")
    private boolean isEnabledFullCheck;

    @KafkaListener(topics = "${kafka.topic.event.sink.payment}",
            containerFactory = "kafkaDgraphPaymentResultListenerContainerFactory")
    public void listen(List<Payment> payments,
                       @Header(KafkaHeaders.RECEIVED_PARTITION_ID) Integer partition,
                       @Header(KafkaHeaders.OFFSET) Long offset) throws InterruptedException {
        try {
            log.info("DgraphPaymentEventListener listen result size: {} partition: {} offset: {}",
                    payments.size(), partition, offset);
            log.debug("PaymentEventListener listen result payments: {}", payments);
            for (Payment payment : payments) {
                DgraphPayment dgraphPayment = paymentToDgraphPaymentConverter.convert(payment);
                fillAdditionalInfo(dgraphPayment, payment);
                dgraphPaymentRepository.insert(dgraphPayment);
            }

        } catch (Exception e) {
            log.warn("Error when PaymentEventListener listen e: ", e);
            Thread.sleep(ListenersConfigurationService.THROTTLING_TIMEOUT);
            throw e;
        }
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
                    log.warn("PaymentEventListener problem with serialize json! listResults: {}", listResults);
                }
            }
        }
    }

}
