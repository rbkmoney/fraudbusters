package com.rbkmoney.fraudbusters.repository.clickhouse.impl;

import com.rbkmoney.damsel.fraudbusters.Refund;
import com.rbkmoney.fraudbusters.constant.EventSource;
import com.rbkmoney.fraudbusters.constant.RefundStatus;
import com.rbkmoney.fraudbusters.fraud.model.FieldModel;
import com.rbkmoney.fraudbusters.repository.AggregationRepository;
import com.rbkmoney.fraudbusters.repository.Repository;
import com.rbkmoney.fraudbusters.repository.clickhouse.mapper.RefundMapper;
import com.rbkmoney.fraudbusters.repository.clickhouse.query.RefundQuery;
import com.rbkmoney.fraudbusters.repository.clickhouse.setter.RefundBatchPreparedStatementSetter;
import com.rbkmoney.fraudbusters.repository.clickhouse.util.FilterUtil;
import com.rbkmoney.fraudbusters.service.dto.FilterDto;
import com.rbkmoney.fraudbusters.util.PaymentTypeByContextResolver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RefundRepository implements Repository<Refund>, AggregationRepository {

    private static final String INSERT = String.format(
            "INSERT INTO %1s (%2s) VALUES (%3s)",
            EventSource.FRAUD_EVENTS_REFUND.getTable(),
            RefundBatchPreparedStatementSetter.FIELDS,
            RefundBatchPreparedStatementSetter.FIELDS_MARK
    );
    private final AggregationStatusGeneralRepositoryImpl aggregationStatusGeneralRepository;
    private final JdbcTemplate jdbcTemplate;
    private final PaymentTypeByContextResolver paymentTypeByContextResolver;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private final RefundMapper refundMapper;

    @Override
    public void insert(Refund refund) {
        throw new UnsupportedOperationException("Method insert is not support!");
    }

    @Override
    public void insertBatch(List<Refund> batch) {
        if (!CollectionUtils.isEmpty(batch)) {
            log.debug("RefundRepository insertBatch batch size: {}", batch.size());
            jdbcTemplate.batchUpdate(
                    INSERT,
                    new RefundBatchPreparedStatementSetter(batch, paymentTypeByContextResolver));
        }
    }

    @Override
    public List<Refund> getByFilter(FilterDto filter) {
        String filters = FilterUtil.appendFilters(filter);
        String query = RefundQuery.SELECT_HISTORY_REFUND + filters;
        MapSqlParameterSource params = FilterUtil.initParams(filter);
        return namedParameterJdbcTemplate.query(query, params, refundMapper);
    }

    @Override
    public Integer countOperationByField(String fieldName, Object value, Long from, Long to) {
        return aggregationStatusGeneralRepository.countOperationByField(
                EventSource.FRAUD_EVENTS_REFUND.getTable(),
                fieldName,
                value,
                from,
                to,
                RefundStatus.succeeded.name()
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
                EventSource.FRAUD_EVENTS_REFUND.getTable(),
                fieldName,
                value,
                from,
                to,
                fieldModels,
                RefundStatus.succeeded.name()
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
                EventSource.FRAUD_EVENTS_REFUND.getTable(),
                fieldName,
                value,
                from,
                to,
                fieldModels,
                RefundStatus.succeeded.name()
        );
    }

    @Override
    public Integer uniqCountOperation(String fieldNameBy, Object value, String fieldNameCount, Long from, Long to) {
        return aggregationStatusGeneralRepository.uniqCountOperation(
                EventSource.FRAUD_EVENTS_REFUND.getTable(),
                fieldNameBy,
                value,
                fieldNameCount,
                from,
                to,
                RefundStatus.succeeded.name()
        );
    }

    @Override
    public Integer uniqCountOperationWithGroupBy(
            String fieldNameBy, Object value, String fieldNameCount, Long from,
            Long to, List<FieldModel> fieldModels) {
        return aggregationStatusGeneralRepository.uniqCountOperationWithGroupBy(
                EventSource.FRAUD_EVENTS_REFUND.getTable(),
                fieldNameBy,
                value,
                fieldNameCount,
                from,
                to,
                fieldModels,
                RefundStatus.succeeded.name()
        );
    }

}
