package com.rbkmoney.fraudbusters.repository.impl;

import com.rbkmoney.damsel.fraudbusters.Payment;
import com.rbkmoney.fraudbusters.constant.EventSource;
import com.rbkmoney.fraudbusters.domain.CheckedPayment;
import com.rbkmoney.fraudbusters.repository.AggregationGeneralRepository;
import com.rbkmoney.fraudbusters.repository.Repository;
import com.rbkmoney.fraudbusters.repository.setter.PaymentBatchPreparedStatementSetter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentRepository implements Repository<CheckedPayment> {

    private final JdbcTemplate jdbcTemplate;

    private static final String INSERT = String.format(
            "INSERT INTO %1s (%2s) VALUES (%3s)",
            EventSource.FRAUD_EVENTS_PAYMENT.getTable(),
            PaymentBatchPreparedStatementSetter.FIELDS,
            PaymentBatchPreparedStatementSetter.FIELDS_MARK);

    @Override
    public void insert(CheckedPayment payment) {
        log.debug("PaymentRepository insert payment: {}", payment);

    }

    @Override
    public void insertBatch(List<CheckedPayment> batch) {
        if (batch != null && !batch.isEmpty()) {
            log.debug("PaymentRepository insertBatch batch: {}", batch);
            jdbcTemplate.batchUpdate(INSERT, new PaymentBatchPreparedStatementSetter(batch));
        }
    }

}
