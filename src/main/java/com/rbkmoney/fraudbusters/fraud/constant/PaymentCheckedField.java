package com.rbkmoney.fraudbusters.fraud.constant;

import java.util.HashMap;
import java.util.Map;

public enum PaymentCheckedField {

    EMAIL("email"),
    IP("ip"),
    FINGERPRINT("fingerprint"),
    COUNTRY_BANK("country_bank"),
    COUNTRY_IP("country_ip"),
    BIN("bin"),
    PAN("pan"),
    CURRENCY("currency"),
    SHOP_ID("shop_id"),
    PARTY_ID("party_id"),
    CARD_TOKEN("card_token");

    private String value;
    private static Map<String, PaymentCheckedField> valueMap = new HashMap<>();

    static {
        for (PaymentCheckedField value : PaymentCheckedField.values()) {
            valueMap.put(value.value, value);
        }
    }

    PaymentCheckedField(String value) {
        this.value = value;
    }

    public static PaymentCheckedField getByValue(String value) {
        return valueMap.get(value);
    }

}
