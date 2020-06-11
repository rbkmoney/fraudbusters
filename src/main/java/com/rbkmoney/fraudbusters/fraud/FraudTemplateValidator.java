package com.rbkmoney.fraudbusters.fraud;

import java.util.List;

public interface FraudTemplateValidator {

    List<String> validate(String template);

}
