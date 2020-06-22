package com.rbkmoney.fraudbusters.resource.handler;

import com.rbkmoney.damsel.fraudbusters.*;
import com.rbkmoney.fraudbusters.fraud.ListTemplateValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.thrift.TException;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class PaymentServiceHandler implements PaymentServiceSrv.Iface {

    private final String fraudPaymentTopic;
    private final ListTemplateValidator paymentTemplatesValidator;
    private final KafkaTemplate<String, FraudPayment> kafkaFraudPaymentTemplate;

    @Override
    public ValidateTemplateResponse validateCompilationTemplate(List<Template> list) throws TException {
        try {
            return paymentTemplatesValidator.validateCompilationTemplate(list);
        } catch (Exception e) {
            log.error("PaymentServiceHandler error when validateCompilationTemplate() e: ", e);
            throw new TException("PaymentServiceHandler error when validateCompilationTemplate() e: ", e);
        }
    }

    @Override
    public void insertFraudPayments(List<FraudPayment> payments) throws TException {
        payments.forEach(p -> kafkaFraudPaymentTemplate.send(fraudPaymentTopic, p));
    }

    @Override
    public void insertPayments(List<Payment> list) throws InsertionException, TException {

    }

    @Override
    public void insertRefunds(List<Refund> list) throws InsertionException, TException {

    }

    @Override
    public void insertChargebacks(List<Chargeback> list) throws InsertionException, TException {

    }
}
