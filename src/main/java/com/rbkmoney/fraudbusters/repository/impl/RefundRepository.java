package com.rbkmoney.fraudbusters.repository.impl;

import com.rbkmoney.damsel.fraudbusters.Refund;
import com.rbkmoney.fraudbusters.constant.EventSource;
import com.rbkmoney.fraudbusters.repository.Repository;
import com.rbkmoney.fraudbusters.repository.impl.analytics.BaseRawParametersGenerator;
import com.rbkmoney.fraudbusters.repository.setter.RefundBatchPreparedStatementSetter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RefundRepository implements Repository<Refund> {

    private final AggregationStatusGeneralRepositoryImpl aggregationStatusGeneralRepository;
    private final JdbcTemplate jdbcTemplate;

    private static final String INSERT = String.format(
            "INSERT INTO %1S (%2S) VALUES (%3S)",
            EventSource.FRAUD_EVENTS_REFUND.getTable(),
            BaseRawParametersGenerator.BASE_RAW_PARAMETERS,
            BaseRawParametersGenerator.BASE_RAW_PARAMETERS_MARK);

    @Override
    public void insert(Refund refund) {
        log.debug("RefundRepository insert refund: {}", refund);

    }

    @Override
    public void insertBatch(List<Refund> batch) {
        if (batch != null && !batch.isEmpty()) {
            log.debug("RefundRepository insertBatch batch size: {}", batch.size());
            jdbcTemplate.batchUpdate(INSERT, new RefundBatchPreparedStatementSetter(batch));
        }
    }

}
