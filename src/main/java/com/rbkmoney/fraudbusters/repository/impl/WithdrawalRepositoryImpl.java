package com.rbkmoney.fraudbusters.repository.impl;

import com.rbkmoney.damsel.fraudbusters.Withdrawal;
import com.rbkmoney.fraudbusters.constant.EventSource;
import com.rbkmoney.fraudbusters.repository.Repository;
import com.rbkmoney.fraudbusters.repository.setter.WithdrawalBatchPreparedStatementSetter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Primary
@Profile("full-prod")
@Component
@RequiredArgsConstructor
public class WithdrawalRepositoryImpl implements Repository<Withdrawal> {

    private static final String INSERT = String.format(
            "INSERT INTO %1s (%2s) VALUES (%3s)",
            EventSource.FRAUD_EVENTS_WITHDRAWAL.getTable(),
            WithdrawalBatchPreparedStatementSetter.FIELDS,
            WithdrawalBatchPreparedStatementSetter.FIELDS_MARK);
    private final JdbcTemplate jdbcTemplate;

    @Override
    public void insert(Withdrawal payment) {
        throw new UnsupportedOperationException("Method insert is not support!");
    }

    @Override
    public void insertBatch(List<Withdrawal> batch) {
        if (batch != null && !batch.isEmpty()) {
            log.debug("insertBatch batch withdrawals: {}", batch);
            jdbcTemplate.batchUpdate(INSERT, new WithdrawalBatchPreparedStatementSetter(batch));
        }
    }

}
