package com.rbkmoney.fraudbusters.resource.handler;

import com.rbkmoney.damsel.fraudbusters.PaymentValidateServiceSrv;
import com.rbkmoney.damsel.fraudbusters.Template;
import com.rbkmoney.damsel.fraudbusters.ValidateTemplateResponse;
import com.rbkmoney.fraudbusters.fraud.ListTemplateValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.thrift.TException;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentTemplateValidatorHandler implements PaymentValidateServiceSrv.Iface {

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
}
