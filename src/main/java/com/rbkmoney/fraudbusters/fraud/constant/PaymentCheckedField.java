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
    MOBILE("mobile"),
    RECURRENT("recurrent"),
    CARD_TOKEN("card_token");

    private static final Map<String, PaymentCheckedField> VALUE_MAP = new HashMap<>();

    static {
        for (PaymentCheckedField value : PaymentCheckedField.values()) {
            VALUE_MAP.put(value.value, value);
        }
    }

    private final String value;

    PaymentCheckedField(String value) {
        this.value = value;
    }

    public static PaymentCheckedField getByValue(String value) {
        return VALUE_MAP.get(value);
    }

}
