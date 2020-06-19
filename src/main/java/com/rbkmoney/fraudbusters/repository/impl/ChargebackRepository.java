package com.rbkmoney.fraudbusters.repository.impl;

import com.rbkmoney.damsel.fraudbusters.Chargeback;
import com.rbkmoney.fraudbusters.constant.EventSource;
import com.rbkmoney.fraudbusters.repository.Repository;
import com.rbkmoney.fraudbusters.repository.impl.analytics.BaseRawParametersGenerator;
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

    private final AggregationStatusGeneralRepositoryImpl aggregationStatusGeneralRepository;
    private final JdbcTemplate jdbcTemplate;

    private static final String INSERT = String.format(
            "INSERT INTO %1S (%2S) VALUES (%3S)",
            EventSource.FRAUD_EVENTS_CHARGEBACK.getTable(),
            BaseRawParametersGenerator.BASE_RAW_PARAMETERS,
            BaseRawParametersGenerator.BASE_RAW_PARAMETERS_MARK);

    @Override
    public void insert(Chargeback chargeback) {
        log.debug("ChargebackRepository insert chargeback: {}", chargeback);

    }

    @Override
    public void insertBatch(List<Chargeback> batch) {
        if (batch != null && !batch.isEmpty()) {
            log.debug("ChargebackRepository insertBatch batch size: {}", batch.size());
            jdbcTemplate.batchUpdate(INSERT, new ChargebackBatchPreparedStatementSetter(batch));
        }
    }

}
