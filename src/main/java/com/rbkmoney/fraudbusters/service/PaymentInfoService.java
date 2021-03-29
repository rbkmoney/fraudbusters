package com.rbkmoney.fraudbusters.service;

import com.rbkmoney.fraudbusters.constant.EventSource;
import com.rbkmoney.fraudbusters.constant.PaymentStatus;
import com.rbkmoney.fraudbusters.domain.CheckedPayment;
import com.rbkmoney.fraudbusters.repository.extractor.CheckedPaymentExtractor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentInfoService {

    private static final String FIELDS = "timestamp, eventTimeHour, eventTime, " +
                                         "id, " +
                                         "email, ip, fingerprint, " +
                                         "bin, maskedPan, cardToken, paymentSystem, paymentTool, " +
                                         "terminal, providerId, bankCountry, " +
                                         "partyId, shopId, " +
                                         "amount, currency, " +
                                         "status, errorCode, errorReason, " +
                                         "payerType, tokenProvider, " +
                                         "checkedTemplate, checkedRule, resultStatus, checkedResultsJson, mobile, " +
                                         "recurrent";

    private final JdbcTemplate jdbcTemplate;

    public CheckedPayment findPaymentByIdAndTimestamp(LocalDate timestamp, String id) {
        log.debug("findPaymentByIdAndTimestamp timestamp: {} id: {}", timestamp, id);
        return jdbcTemplate.query("select " + FIELDS + " from " + EventSource.FRAUD_EVENTS_PAYMENT.getTable() +
                                  " where timestamp = ? and id = ? and status = ? ",
                List.of(timestamp, id, PaymentStatus.captured.name()).toArray(), new CheckedPaymentExtractor()
        );
    }

}
