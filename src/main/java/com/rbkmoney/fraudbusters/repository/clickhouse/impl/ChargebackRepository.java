package com.rbkmoney.fraudbusters.repository.clickhouse.impl;

import com.rbkmoney.damsel.fraudbusters.Chargeback;
import com.rbkmoney.fraudbusters.constant.ChargebackStatus;
import com.rbkmoney.fraudbusters.constant.EventSource;
import com.rbkmoney.fraudbusters.fraud.model.FieldModel;
import com.rbkmoney.fraudbusters.repository.AggregationRepository;
import com.rbkmoney.fraudbusters.repository.Repository;
import com.rbkmoney.fraudbusters.repository.clickhouse.mapper.ChargebackMapper;
import com.rbkmoney.fraudbusters.repository.clickhouse.query.ChargeBackQuery;
import com.rbkmoney.fraudbusters.repository.clickhouse.setter.ChargebackBatchPreparedStatementSetter;
import com.rbkmoney.fraudbusters.repository.clickhouse.util.FilterUtil;
import com.rbkmoney.fraudbusters.service.dto.FilterDto;
import com.rbkmoney.fraudbusters.util.PaymentTypeByContextResolver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChargebackRepository implements Repository<Chargeback>, AggregationRepository {

    private static final String INSERT = String.format(
            "INSERT INTO %1s (%2s) VALUES (%3s)",
            EventSource.FRAUD_EVENTS_CHARGEBACK.getTable(),
            ChargebackBatchPreparedStatementSetter.FIELDS,
            ChargebackBatchPreparedStatementSetter.FIELDS_MARK
    );

    private final AggregationStatusGeneralRepositoryImpl aggregationStatusGeneralRepository;
    private final JdbcTemplate jdbcTemplate;
    private final PaymentTypeByContextResolver paymentTypeByContextResolver;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private final ChargebackMapper chargebackMapper;


    @Override
    public void insert(Chargeback chargeback) {
        throw new UnsupportedOperationException("Method insertBatch is not support!");
    }

    @Override
    public void insertBatch(List<Chargeback> batch) {
        if (batch != null && !batch.isEmpty()) {
            log.debug("ChargebackRepository insertBatch batch: {}", batch);
            jdbcTemplate.batchUpdate(
                    INSERT,
                    new ChargebackBatchPreparedStatementSetter(batch, paymentTypeByContextResolver));
        }
    }

    @Override
    public List<Chargeback> getByFilter(FilterDto filter) {
        String filters = FilterUtil.appendFilters(filter);
        String query = ChargeBackQuery.SELECT_HISTORY_CHARGEBACK + filters;
        MapSqlParameterSource params = FilterUtil.initParams(filter);
        return namedParameterJdbcTemplate.query(query, params, chargebackMapper);
    }

    @Override
    public Integer countOperationByField(String fieldName, Object value, Long from, Long to) {
        return aggregationStatusGeneralRepository.countOperationByField(
                EventSource.FRAUD_EVENTS_CHARGEBACK.getTable(),
                fieldName,
                value,
                from,
                to,
                ChargebackStatus.accepted.name()
        );
    }

    @Override
    public Integer countOperationByFieldWithGroupBy(
            String fieldName,
            Object value,
            Long from,
            Long to,
            List<FieldModel> fieldModels) {
        return aggregationStatusGeneralRepository.countOperationByFieldWithGroupBy(
                EventSource.FRAUD_EVENTS_CHARGEBACK.getTable(),
                fieldName,
                value,
                from,
                to,
                fieldModels,
                ChargebackStatus.accepted.name()
        );
    }

    @Override
    public Long sumOperationByFieldWithGroupBy(
            String fieldName,
            Object value,
            Long from,
            Long to,
            List<FieldModel> fieldModels) {
        return aggregationStatusGeneralRepository.sumOperationByFieldWithGroupBy(
                EventSource.FRAUD_EVENTS_CHARGEBACK.getTable(),
                fieldName,
                value,
                from,
                to,
                fieldModels,
                ChargebackStatus.accepted.name()
        );
    }

    @Override
    public Integer uniqCountOperation(String fieldNameBy, Object value, String fieldNameCount, Long from, Long to) {
        return aggregationStatusGeneralRepository.uniqCountOperation(
                EventSource.FRAUD_EVENTS_CHARGEBACK.getTable(),
                fieldNameBy,
                value,
                fieldNameCount,
                from,
                to,
                ChargebackStatus.accepted.name()
        );
    }

    @Override
    public Integer uniqCountOperationWithGroupBy(
            String fieldNameBy,
            Object value,
            String fieldNameCount,
            Long from,
            Long to,
            List<FieldModel> fieldModels) {
        return aggregationStatusGeneralRepository.uniqCountOperationWithGroupBy(
                EventSource.FRAUD_EVENTS_CHARGEBACK.getTable(),
                fieldNameBy,
                value,
                fieldNameCount,
                from,
                to,
                fieldModels,
                ChargebackStatus.accepted.name()
        );
    }
}
