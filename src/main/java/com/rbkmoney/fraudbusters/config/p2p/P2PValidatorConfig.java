package com.rbkmoney.fraudbusters.config.p2p;

import com.rbkmoney.fraudbusters.fraud.FraudTemplateValidator;
import com.rbkmoney.fraudbusters.fraud.ListTemplateValidator;
import com.rbkmoney.fraudbusters.fraud.validator.ListTemplateValidatorImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class P2PValidatorConfig {

    @Bean
    public ListTemplateValidator p2pTemplatesValidator(FraudTemplateValidator p2PTemplateValidator) {
        return new ListTemplateValidatorImpl(p2PTemplateValidator);
    }

}
