package com.rbkmoney.fraudbusters.repository.impl;

import com.rbkmoney.damsel.fraudbusters.Refund;
import com.rbkmoney.fraudbusters.constant.EventSource;
import com.rbkmoney.fraudbusters.repository.Repository;
import com.rbkmoney.fraudbusters.repository.setter.RefundBatchPreparedStatementSetter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RefundRepository implements Repository<Refund> {

    private final JdbcTemplate jdbcTemplate;

    private static final String INSERT = String.format(
            "INSERT INTO %1s (%2s) VALUES (%3s)",
            EventSource.FRAUD_EVENTS_REFUND.getTable(),
            RefundBatchPreparedStatementSetter.FIELDS,
            RefundBatchPreparedStatementSetter.FIELDS_MARK);

    @Override
    public void insert(Refund refund) {
        log.debug("RefundRepository insert refund: {}", refund);

    }

    @Override
    public void insertBatch(List<Refund> batch) {
        if (!CollectionUtils.isEmpty(batch)) {
            log.debug("RefundRepository insertBatch batch size: {}", batch.size());
            jdbcTemplate.batchUpdate(INSERT, new RefundBatchPreparedStatementSetter(batch));
        }
    }

}
