package com.rbkmoney.fraudbusters.repository.query;

import com.rbkmoney.fraudbusters.constant.EventSource;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ChargeBackQuery {

    public static final String SELECT_HISTORY_CHARGEBACK = "" +
            "SELECT " +
            "    eventTime, " +
            "    partyId, " +
            "    shopId, " +
            "    email, " +
            "    amount as amount, " +
            "    currency, " +
            "    id, " +
            "    bankCountry, " +
            "    cardToken, " +
            "    bin, " +
            "    maskedPan, " +
            "    paymentSystem, " +
            "    providerId, " +
            "    status, " +
            "    ip, " +
            "    fingerprint, " +
            "    terminal, " +
            "    paymentId, " +
            "    chargebackCode, " +
            "    category " +
            " FROM " +
            EventSource.FRAUD_EVENTS_CHARGEBACK.getTable() +
            " WHERE " +
            "    timestamp >= toDate(:from) " +
            "    and timestamp <= toDate(:to) " +
            "    and toDateTime(eventTime) >= toDateTime(:from) " +
            "    and toDateTime(eventTime) <= toDateTime(:to) ";
}
