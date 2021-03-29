package com.rbkmoney.fraudbusters.fraud.constant;

import java.util.HashMap;
import java.util.Map;

public enum P2PCheckedField {

    EMAIL("email"),
    IP("ip"),
    FINGERPRINT("fingerprint"),
    COUNTRY_BANK("country_bank"),
    BIN("bin"),
    PAN("pan"),
    CURRENCY("currency"),
    IDENTITY_ID("identity_id"),
    CARD_TOKEN_FROM("card_token_from"),
    CARD_TOKEN_TO("card_token_to");

    private static final Map<String, P2PCheckedField> valueMap = new HashMap<>();

    static {
        for (P2PCheckedField value : P2PCheckedField.values()) {
            valueMap.put(value.value, value);
        }
    }

    private final String value;

    P2PCheckedField(String value) {
        this.value = value;
    }

    public static P2PCheckedField getByValue(String value) {
        return valueMap.get(value);
    }

}
