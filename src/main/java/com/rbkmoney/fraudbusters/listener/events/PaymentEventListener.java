package com.rbkmoney.fraudbusters.listener.events;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rbkmoney.damsel.fraudbusters.Payment;
import com.rbkmoney.fraudbusters.config.KafkaConfig;
import com.rbkmoney.fraudbusters.converter.PaymentToCheckedPaymentConverter;
import com.rbkmoney.fraudbusters.converter.PaymentToPaymentModelConverter;
import com.rbkmoney.fraudbusters.domain.CheckedPayment;
import com.rbkmoney.fraudbusters.domain.CheckedResultModel;
import com.rbkmoney.fraudbusters.repository.Repository;
import com.rbkmoney.fraudbusters.stream.FullTemplateVisitorImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentEventListener {

    private final Repository<Payment> repository;
    private final FullTemplateVisitorImpl fullTemplateVisitor;
    private final PaymentToPaymentModelConverter paymentToPaymentModelConverter;
    private final PaymentToCheckedPaymentConverter paymentToCheckedPaymentConverter;

    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "${kafka.topic.event.sink.payment}", containerFactory = "kafkaPaymentResultListenerContainerFactory")
    public void listen(List<Payment> payments, @Header(KafkaHeaders.RECEIVED_PARTITION_ID) Integer partition,
                       @Header(KafkaHeaders.OFFSET) Long offset) throws InterruptedException {
        try {
            log.info("PaymentEventListener listen result size: {} partition: {} offset: {}", payments.size(), partition, offset);
            payments.stream()
                    .map(payment -> {
                        CheckedPayment checkedPayment = paymentToCheckedPaymentConverter.convert(payment);
                        List<CheckedResultModel> listResults = fullTemplateVisitor.visit(paymentToPaymentModelConverter.convert(payment));
                        if (!CollectionUtils.isEmpty(listResults)) {
                            checkedPayment.setCheckedTemplate(listResults.get(0).getCheckedTemplate());
                            checkedPayment.setResultStatus(listResults.get(0).getResultModel().getResultStatus().name());
                            try {
                                checkedPayment.setCheckedResultsJson(objectMapper.writeValueAsString(listResults));
                            } catch (JsonProcessingException e) {
                                log.warn("PaymentEventListener problem with serialize json!");
                            }
                        }
                        return checkedPayment;
                    })
                    .collect(Collectors.toList());
            repository.insertBatch(payments);
        } catch (Exception e) {
            log.warn("Error when PaymentEventListener listen e: ", e);
            Thread.sleep(KafkaConfig.THROTTLING_TIMEOUT);
            throw e;
        }
    }
}
