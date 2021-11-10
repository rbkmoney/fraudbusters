package com.rbkmoney.fraudbusters.fraud.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum DgraphEntity {

    TOKEN("Token", 100),
    PAYMENT("Payment", 10),
    FINGERPRINT("Fingerprint", 50),
    EMAIL("Email", 60),
    PARTY_SHOP("PartyShop", 90),
    COUNTRY("Country", 30),
    IP("IP", 40),
    BIN("Bin", 40),
    FRAUD_PAYMENT("FraudPayment", 10),
    REFUND("Refund", 10),
    CHARGEBACK("Chargeback", 10),
    WITHDRAWAL("Withdrawal", 10);

    private final String typeName;
    private final int weight;

}
