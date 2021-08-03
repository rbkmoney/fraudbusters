package com.rbkmoney.fraudbusters.repository.query;

import com.rbkmoney.fraudbusters.constant.EventSource;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FraudResultQuery {

    public static final String SELECT_HISTORY_FRAUD_RESULT = "" +
            "SELECT " +
            "    eventTime, " +
            "    partyId, " +
            "    shopId, " +
            "    email, " +
            "    amount as amount, " +
            "    currency, " +
            "    bankCountry, " +
            "    cardToken, " +
            "    ip, " +
            "    fingerprint, " +
            "    invoiceId, " +
            "    maskedPan, " +
            "    bin, " +
            "    bankName, " +
            "    paymentId as id, " +
            "    resultStatus, " +
            "    checkedRule, " +
            "    checkedTemplate, " +
            "    mobile, " +
            "    recurrent " +
            " FROM " +
            EventSource.FRAUD_EVENTS_UNIQUE.getTable() +
            " WHERE " +
            "    timestamp >= toDate(:from) " +
            "    and timestamp <= toDate(:to) " +
            "    and toDateTime(eventTime) >= toDateTime(:from) " +
            "    and toDateTime(eventTime) <= toDateTime(:to) ";
}
