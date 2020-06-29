package com.rbkmoney.fraudbusters.repository.impl;

import com.rbkmoney.damsel.fraudbusters.Chargeback;
import com.rbkmoney.fraudbusters.constant.EventSource;
import com.rbkmoney.fraudbusters.repository.Repository;
import com.rbkmoney.fraudbusters.repository.setter.ChargebackBatchPreparedStatementSetter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChargebackRepository implements Repository<Chargeback> {

    private final JdbcTemplate jdbcTemplate;

    private static final String INSERT = String.format(
            "INSERT INTO %1s (%2s) VALUES (%3s)",
            EventSource.FRAUD_EVENTS_CHARGEBACK.getTable(),
            ChargebackBatchPreparedStatementSetter.FIELDS,
            ChargebackBatchPreparedStatementSetter.FIELDS_MARK);

    @Override
    public void insert(Chargeback chargeback) {
        log.debug("ChargebackRepository insert chargeback: {}", chargeback);

    }

    @Override
    public void insertBatch(List<Chargeback> batch) {
        if (batch != null && !batch.isEmpty()) {
            log.debug("ChargebackRepository insertBatch batch: {}", batch);
            jdbcTemplate.batchUpdate(INSERT, new ChargebackBatchPreparedStatementSetter(batch));
        }
    }

}
