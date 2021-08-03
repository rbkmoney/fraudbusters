package com.rbkmoney.fraudbusters.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum FraudPaymentField {

    FRAUD_TYPE("fraudType"),
    COMMENT("comment");

    @Getter
    private final String value;

}
