package com.rbkmoney.fraudbusters.repository.clickhouse.query;

import com.rbkmoney.fraudbusters.constant.EventSource;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FraudPaymentQuery {

    public static final String SELECT_HISTORY_FRAUD_PAYMENT = String.format("""
            SELECT
                eventTime,
                partyId,
                shopId,
                email,
                phone,
                amount as amount,
                currency,
                id,
                cardToken,
                cardCategory,
                bin,
                maskedPan,
                bankCountry,
                fingerprint,
                ip,
                status,
                errorReason,
                errorCode,
                paymentSystem,
                paymentCountry,
                paymentTool,
                providerId,
                terminal,
                fraudType,
                comment
             FROM
            %s
             WHERE
                timestamp >= toDate(:from)
                and timestamp <= toDate(:to)
                and toDateTime(eventTime) >= toDateTime(:from)
                and toDateTime(eventTime) <= toDateTime(:to)""",
                    EventSource.FRAUD_EVENTS_FRAUD_PAYMENT.getTable());
}
