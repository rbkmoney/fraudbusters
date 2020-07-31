package com.rbkmoney.fraudbusters.repository.impl;

import com.rbkmoney.damsel.fraudbusters.FraudPayment;
import com.rbkmoney.fraudbusters.repository.Repository;
import com.rbkmoney.fraudbusters.repository.setter.FraudPaymentBatchPreparedStatementSetter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class FraudPaymentRepository implements Repository<FraudPayment> {

    private final JdbcTemplate jdbcTemplate;

    private static final String INSERT = "INSERT INTO fraud.fraud_payment " +
            " (timestamp, id, eventTime, partyId, shopId, amount, currency, payerType, paymentToolType, cardToken, paymentSystem, " +
            "maskedPan, issuerCountry, email, ip, fingerprint, status, rrn, providerId, terminalId, tempalateId, description)" +
            " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    @Override
    public void insert(FraudPayment value) {
        throw new UnsupportedOperationException("Method insertBatch is not support!");
    }

    @Override
    public void insertBatch(List<FraudPayment> payments) {
        log.debug("FraudPaymentRepository insertBatch payments: {}", payments);
        if (!CollectionUtils.isEmpty(payments)) {
            jdbcTemplate.batchUpdate(INSERT, new FraudPaymentBatchPreparedStatementSetter(payments));
        }
    }

}
