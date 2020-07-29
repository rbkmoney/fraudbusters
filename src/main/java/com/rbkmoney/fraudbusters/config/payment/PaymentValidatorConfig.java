package com.rbkmoney.fraudbusters.config.payment;

import com.rbkmoney.fraudbusters.fraud.FraudTemplateValidator;
import com.rbkmoney.fraudbusters.fraud.ListTemplateValidator;
import com.rbkmoney.fraudbusters.fraud.validator.ListTemplateValidatorImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PaymentValidatorConfig {

    @Bean
    public ListTemplateValidator paymentTemplatesValidator(FraudTemplateValidator paymentTemplateValidator) {
        return new ListTemplateValidatorImpl(paymentTemplateValidator);
    }

}
