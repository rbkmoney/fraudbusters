package com.rbkmoney.fraudbusters.fraud.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class DgraphFieldConditionTemplate {

    public static final String BIN = "eq(cardBin, \"%s\")";
    public static final String EMAIL = "eq(userEmail, \"%s\")";
    public static final String IP = "eq(ipAddress, \"%s\")";
    public static final String FINGERPRINT = "eq(fingerprintData, \"%s\")";
    public static final String CARD_TOKEN = "eq(tokenId, \"%s\")";
    public static final String PARTY_ID = "eq(partyId, \"%s\")";
    public static final String SHOP_ID = "eq(shopId, \"%s\")";
    public static final String PAN = "eq(maskedPan, \"%s\")";
    public static final String COUNTRY_BANK = "eq(countryName, \"%s\")";
    public static final String CURRENCY = "eq(currencyCode, \"%s\")";
    public static final String MOBILE = "eq(mobile, %s)";
    public static final String RECURRENT = "eq(recurrent, %s)";

}
