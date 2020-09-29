package com.rbkmoney.fraudbusters.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum EventSource {

    FRAUD_EVENTS_PAYMENT("fraud.payment"),
    FRAUD_EVENTS_FRAUD_PAYMENT("fraud.fraud_payment"),
    FRAUD_EVENTS_REFUND("fraud.refund"),
    FRAUD_EVENTS_CHARGEBACK("fraud.chargeback"),
    FRAUD_EVENTS_UNIQUE("fraud.events_unique"),
    FRAUD_EVENTS_P_TO_P("fraud.events_p_to_p");

    @Getter
    private final String table;

}
