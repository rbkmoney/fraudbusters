package com.rbkmoney.fraudbusters.config;

import com.rbkmoney.fraudbusters.fraud.FraudTemplateValidator;
import com.rbkmoney.fraudbusters.fraud.ListTemplateValidator;
import com.rbkmoney.fraudbusters.fraud.validator.ListTemplateValidatorImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ValidatorConfig {

    @Bean
    public ListTemplateValidator paymentTemplatesValidator(FraudTemplateValidator p2PTemplateValidator) {
        return new ListTemplateValidatorImpl(p2PTemplateValidator);
    }

    @Bean
    public ListTemplateValidator p2pTemplatesValidator(FraudTemplateValidator paymentTemplateValidator) {
        return new ListTemplateValidatorImpl(paymentTemplateValidator);
    }

}
