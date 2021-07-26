package com.rbkmoney.fraudbusters.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum PaymentField {

    EVENT_TIME("eventTime"),
    SHOP_ID("shopId"),
    PARTY_ID("partyId"),
    EMAIL("email"),
    IP("ip"),
    FINGERPRINT("fingerprint"),
    BANK_COUNTRY("bankCountry"),
    CARD_TOKEN("cardToken"),
    AMOUNT("amount"),
    CURRENCY("currency"),
    ID("id"),
    STATUS("status"),
    ERROR_REASON("errorReason"),
    ERROR_CODE("errorCode"),
    PAYMENT_SYSTEM("paymentSystem"),
    PAYMENT_COUNTRY("paymentCountry"),
    PAYMENT_TOOL("paymentTool"),
    PROVIDER_ID("providerId"),
    TERMINAL("terminal"),
    MASKED_PAN("maskedPan"),
    INVOICE_ID("invoiceId");

    @Getter
    private final String value;

}
