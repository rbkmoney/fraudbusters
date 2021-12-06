package com.rbkmoney.fraudbusters.fraud.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class DgraphPaymentFilterField {

    public static final String TOKEN = "cardToken";
    public static final String BIN = "bin";
    public static final String EMAIL = "contactEmail";
    public static final String FINGERPRINT = "fingerprint";
    public static final String IP = "operationIp";
    public static final String PARTY = "party";
    public static final String SHOP = "shop";
    public static final String COUNTRY = "country";
    public static final String CURRENCY = "currency";
    public static final String FRAUD_PAYMENT = "fraudPayment";
    public static final String PAYMENT = "sourcePayment";

}
