package com.rbkmoney.fraudbusters.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum ChargebackField {

    CATEGORY("category"),
    CHARGEBACK_CODE("chargebackCode");

    @Getter
    private final String value;

}
