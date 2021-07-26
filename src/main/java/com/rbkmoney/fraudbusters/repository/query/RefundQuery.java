package com.rbkmoney.fraudbusters.repository.query;

import com.rbkmoney.fraudbusters.constant.EventSource;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RefundQuery {

    public static final String SELECT_HISTORY_REFUND = "" +
            "SELECT " +
            "    eventTime, " +
            "    partyId, " +
            "    shopId, " +
            "    email, " +
            "    amount / 100 as amount, " +
            "    currency, " +
            "    id, " +
            "    cardToken, " +
            "    bankCountry, " +
            "    fingerprint, " +
            "    ip, " +
            "    status, " +
            "    errorReason, " +
            "    errorCode, " +
            "    paymentSystem, " +
            "    providerId, " +
            "    terminal " +
            " FROM " +
            EventSource.FRAUD_EVENTS_REFUND.getTable() +
            " WHERE " +
            "    timestamp >= toDate(:from) " +
            "    and timestamp <= toDate(:to) " +
            "    and toDateTime(eventTime) >= toDateTime(:from) " +
            "    and toDateTime(eventTime) <= toDateTime(:to) ";
}
