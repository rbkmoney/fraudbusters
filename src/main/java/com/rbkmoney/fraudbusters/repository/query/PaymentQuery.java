package com.rbkmoney.fraudbusters.repository.query;

import com.rbkmoney.fraudbusters.constant.EventSource;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PaymentQuery {

    public static final String SELECT_HISTORY_PAYMENT = String.format("""
                    SELECT
                         eventTime,
                        partyId,
                        shopId,
                        email,
                        amount as amount,
                        currency,
                        id,
                        cardToken,
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
                        mobile,
                        recurrent
                     FROM
                     %s
                     WHERE
                        timestamp >= toDate(:from)
                        and timestamp <= toDate(:to)
                        and toDateTime(eventTime) >= toDateTime(:from)
                        and toDateTime(eventTime) <= toDateTime(:to)""",
            EventSource.FRAUD_EVENTS_PAYMENT.getTable());
}
