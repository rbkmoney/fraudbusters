package com.rbkmoney.fraudbusters.fraud.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum DgraphEntity {

    TOKEN("Token"),
    PAYMENT("Payment"),
    FINGERPRINT("Fingerprint"),
    EMAIL("Email"),
    PARTY("Party"),
    SHOP("Shop"),
    COUNTRY("Country"),
    IP("IP"),
    BIN("Bin"),
    CURRENCY("Currency"),
    FRAUD_PAYMENT("FraudPayment"),
    REFUND("Refund"),
    CHARGEBACK("Chargeback"),
    WITHDRAWAL("Withdrawal");

    private final String typeName;

}
