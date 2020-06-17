package com.rbkmoney.fraudbusters.resource.handler;

import com.rbkmoney.damsel.fraudbusters.*;
import com.rbkmoney.fraudbusters.fraud.ListTemplateValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.thrift.TException;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentTemplateValidatorHandler implements PaymentServiceSrv.Iface {

    private final ListTemplateValidator paymentTemplatesValidator;

    @Override
    public ValidateTemplateResponse validateCompilationTemplate(List<Template> list) throws TException {
        try {
            return paymentTemplatesValidator.validateCompilationTemplate(list);
        } catch (Exception e) {
            log.error("PaymentTemplateValidatorHandler error when validateCompilationTemplate() e: ", e);
            throw new TException("PaymentTemplateValidatorHandler error when validateCompilationTemplate() e: ", e);
        }
    }

    @Override
    public void insertFraudPayments(List<FraudPayment> list) throws TException {

    }

    @Override
    public void insertPayments(List<Payment> list) throws TException {

    }

    @Override
    public void insertRefunds(List<Refund> list) throws TException {

    }

    @Override
    public void insertChargebacks(List<Chargeback> list) throws TException {

    }

}
