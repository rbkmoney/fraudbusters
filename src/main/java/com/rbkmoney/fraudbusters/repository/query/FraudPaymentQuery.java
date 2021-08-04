package com.rbkmoney.fraudbusters.repository.query;

import com.rbkmoney.fraudbusters.constant.EventSource;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FraudPaymentQuery {

    public static final String SELECT_HISTORY_FRAUD_PAYMENT = "" +
            "SELECT " +
            "    eventTime, " +
            "    partyId, " +
            "    shopId, " +
            "    email, " +
            "    amount as amount, " +
            "    currency, " +
            "    id, " +
            "    cardToken, " +
            "    bin, " +
            "    maskedPan, " +
            "    bankCountry, " +
            "    fingerprint, " +
            "    ip, " +
            "    status, " +
            "    errorReason, " +
            "    errorCode, " +
            "    paymentSystem, " +
            "    paymentCountry, " +
            "    paymentTool, " +
            "    providerId, " +
            "    terminal, " +
            "    fraudType, " +
            "    comment " +
            " FROM " +
            EventSource.FRAUD_EVENTS_FRAUD_PAYMENT.getTable() +
            " WHERE " +
            "    timestamp >= toDate(:from) " +
            "    and timestamp <= toDate(:to) " +
            "    and toDateTime(eventTime) >= toDateTime(:from) " +
            "    and toDateTime(eventTime) <= toDateTime(:to) ";
}
