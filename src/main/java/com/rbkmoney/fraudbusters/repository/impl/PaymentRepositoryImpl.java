package com.rbkmoney.fraudbusters.repository.impl;

import com.rbkmoney.fraudbusters.constant.*;
import com.rbkmoney.fraudbusters.domain.CheckedPayment;
import com.rbkmoney.fraudbusters.fraud.model.FieldModel;
import com.rbkmoney.fraudbusters.repository.PaymentRepository;
import com.rbkmoney.fraudbusters.repository.Repository;
import com.rbkmoney.fraudbusters.repository.extractor.CountExtractor;
import com.rbkmoney.fraudbusters.repository.extractor.SumExtractor;
import com.rbkmoney.fraudbusters.repository.mapper.CheckedPaymentMapper;
import com.rbkmoney.fraudbusters.repository.query.PaymentQuery;
import com.rbkmoney.fraudbusters.repository.setter.PaymentBatchPreparedStatementSetter;
import com.rbkmoney.fraudbusters.repository.util.AggregationUtil;
import com.rbkmoney.fraudbusters.service.dto.FilterDto;
import com.rbkmoney.fraudbusters.util.CompositeIdUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Primary
@Profile("full-prod")
@Component
@RequiredArgsConstructor
public class PaymentRepositoryImpl implements Repository<CheckedPayment>, PaymentRepository {

    private static final String PAGE_CONTENT_FILTER = " and (id %s :id or (status != :status and id = :id)) ";
    private static final String TABLE = EventSource.FRAUD_EVENTS_PAYMENT.getTable();
    private static final String INSERT = String.format(
            "INSERT INTO %1s (%2s) VALUES (%3s)",
            EventSource.FRAUD_EVENTS_PAYMENT.getTable(),
            PaymentBatchPreparedStatementSetter.FIELDS,
            PaymentBatchPreparedStatementSetter.FIELDS_MARK
    );
    private final JdbcTemplate jdbcTemplate;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private final CheckedPaymentMapper checkedPaymentMapper;

    @Override
    public void insert(CheckedPayment payment) {
        throw new UnsupportedOperationException("Method insert is not support!");
    }

    @Override
    public void insertBatch(List<CheckedPayment> batch) {
        if (batch != null && !batch.isEmpty()) {
            log.debug("PaymentRepository insertBatch batch: {}", batch);
            jdbcTemplate.batchUpdate(INSERT, new PaymentBatchPreparedStatementSetter(batch));
        }
    }

    @Override
    public List<CheckedPayment> getByFilter(FilterDto filter) {
        StringBuilder filters = new StringBuilder();
        Map<PaymentField, String> filterFields = filter.getSearchPatterns();
        if (!CollectionUtils.isEmpty(filterFields)) {
            filterFields.forEach((key, value) ->
                    filters.append(" and like(").append(key.getValue()).append(",'").append(value).append("')"));
        }
        if (Objects.nonNull(filter.getLastId())) {
            if (SortOrder.DESC.equals(filter.getSort().getOrder())) {
                filters.append(String.format(PAGE_CONTENT_FILTER, "<"));
            } else {
                filters.append(String.format(PAGE_CONTENT_FILTER, ">"));
            }
        }
        String sorting = String.format("ORDER BY (eventTime, id) %s ", filter.getSort().getOrder().name());
        String limit = " LIMIT :size ";
        String query = PaymentQuery.SELECT_HISTORY_PAYMENT +
                filters.toString() +
                sorting +
                limit;
        MapSqlParameterSource params = buildParams(filter);
        return namedParameterJdbcTemplate.query(query, params, checkedPaymentMapper);
    }

    private MapSqlParameterSource buildParams(FilterDto filter) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        if (Objects.nonNull(filter.getLastId())) {
            List<String> compositeId = CompositeIdUtil.extract(filter.getLastId());
            if (compositeId.size() == 2) {
                params.addValue(QueryParamName.ID, compositeId.get(0))
                        .addValue(QueryParamName.STATUS, compositeId.get(1));
            }
        }
        params.addValue(QueryParamName.FROM, filter.getTimeFrom())
                .addValue(QueryParamName.TO, filter.getTimeTo())
                .addValue(QueryParamName.SIZE, filter.getSize());
        return params;
    }

    @Override
    public Integer countOperationByField(String fieldName, Object value, Long from, Long to) {
        String sql = String.format(
                "select %1$s, count() as cnt " +
                        "from %2$s " +
                        "where timestamp >= ? " +
                        "and timestamp <= ? " +
                        "and eventTime >= ? " +
                        "and eventTime <= ? " +
                "and %1$s = ?  and status != ?" +
                "group by %1$s", fieldName, TABLE);
        List<Object> params = AggregationUtil.generateStatusParams(from, to, value, PaymentStatus.captured.name());
        log.debug("AggregationGeneralRepositoryImpl countOperationByField sql: {} params: {}", sql, params);
        return jdbcTemplate.query(sql, params.toArray(), new CountExtractor());
    }

    @Override
    public Integer countOperationByFieldWithGroupBy(
            String fieldName,
            Object value,
            Long from,
            Long to,
            List<FieldModel> fieldModels) {
        StringBuilder sql = new StringBuilder(String.format(
                "select %1$s, count() as cnt " +
                "from %2$s " +
                "where timestamp >= ? " +
                "and timestamp <= ? " +
                "and eventTime >= ? " +
                "and eventTime <= ? " +
                "and %1$s = ? and status != ? ", fieldName, EventSource.FRAUD_EVENTS_PAYMENT.getTable()));
        StringBuilder sqlGroupBy = new StringBuilder(String.format("group by %1$s", fieldName));
        StringBuilder resultSql = AggregationUtil.appendGroupingFields(fieldModels, sql, sqlGroupBy);
        List<Object> params =
                AggregationUtil.generateParams(from, to, fieldModels, value, PaymentStatus.captured.name());
        log.debug("PaymentRepositoryImpl countOperationByFieldWithGroupBy sql: {} params: {}", sql, params);
        return jdbcTemplate.query(resultSql.toString(), params.toArray(), new CountExtractor());
    }

    @Override
    public Long sumOperationByFieldWithGroupBy(
            String fieldName,
            Object value,
            Long from,
            Long to,
            List<FieldModel> fieldModels) {
        StringBuilder sql = new StringBuilder(String.format(
                "select %1$s, sum(amount) as sum " +
                "from %2$s " +
                "where timestamp >= ? " +
                "and timestamp <= ? " +
                "and eventTime >= ? " +
                "and eventTime <= ? " +
                "and %1$s = ? and status != ? ", fieldName, EventSource.FRAUD_EVENTS_PAYMENT.getTable()));
        StringBuilder sqlGroupBy = new StringBuilder(String.format("group by %1$s", fieldName));
        StringBuilder resultSql = AggregationUtil.appendGroupingFields(fieldModels, sql, sqlGroupBy);
        List<Object> params =
                AggregationUtil.generateParams(from, to, fieldModels, value, PaymentStatus.captured.name());
        log.debug("PaymentRepositoryImpl sumOperationSuccessWithGroupBy sql: {} params: {}", sql, params);
        return jdbcTemplate.query(resultSql.toString(), params.toArray(), new SumExtractor());
    }

    @Override
    public Integer uniqCountOperation(String fieldNameBy, Object value, String fieldNameCount, Long from, Long to) {
        String sql = String.format(
                "select %1$s, uniq(%2$s) as cnt " +
                "from %3$s " +
                "where timestamp >= ? " +
                "and timestamp <= ? " +
                "and eventTime >= ? " +
                "and eventTime <= ? " +
                "and %1$s = ? and status != ?" +
                "group by %1$s", fieldNameBy, fieldNameCount, TABLE);
        List<Object> params = AggregationUtil.generateStatusParams(from, to, value, PaymentStatus.captured.name());
        log.debug("AggregationGeneralRepositoryImpl uniqCountOperation sql: {} params: {}", sql, params);
        return jdbcTemplate.query(sql, params.toArray(), new CountExtractor());
    }

    @Override
    public Integer uniqCountOperationWithGroupBy(
            String fieldNameBy, Object value, String fieldNameCount, Long from, Long to,
            List<FieldModel> fieldModels) {
        StringBuilder sql = new StringBuilder(String.format(
                "select %1$s, uniq(%2$s) as cnt " +
                "from %3$s " +
                "where timestamp >= ? " +
                "and timestamp <= ? " +
                "and eventTime >= ? " +
                "and eventTime <= ? " +
                "and %1$s = ?  and status != ?",
                fieldNameBy,
                fieldNameCount,
                EventSource.FRAUD_EVENTS_PAYMENT.getTable()
        ));
        StringBuilder sqlGroupBy = new StringBuilder(String.format("group by %1$s", fieldNameBy));
        StringBuilder resultSql = AggregationUtil.appendGroupingFields(fieldModels, sql, sqlGroupBy);
        List<Object> params =
                AggregationUtil.generateParams(from, to, fieldModels, value, PaymentStatus.captured.name());
        String sqlResult = resultSql.toString();
        log.debug("uniqCountOperationWithGroupBy sql: {} params: {}", sqlResult, params);
        return jdbcTemplate.query(sqlResult, params.toArray(), new CountExtractor());
    }

    @Override
    public Integer countOperationSuccessWithGroupBy(
            String fieldName, Object value, Long from, Long to,
            List<FieldModel> fieldModels) {
        StringBuilder sql = new StringBuilder(String.format(
                "select %1$s, count() as cnt " +
                "from %2$s " +
                "where timestamp >= ? " +
                "and timestamp <= ? " +
                "and eventTime >= ? " +
                "and eventTime <= ? " +
                "and %1$s = ? and status = ? ", fieldName, EventSource.FRAUD_EVENTS_PAYMENT.getTable()));
        StringBuilder sqlGroupBy = new StringBuilder(String.format("group by %1$s", fieldName));
        StringBuilder resultSql = AggregationUtil.appendGroupingFields(fieldModels, sql, sqlGroupBy);
        List<Object> params =
                AggregationUtil.generateParams(from, to, fieldModels, value, PaymentStatus.captured.name());
        log.debug("PaymentRepositoryImpl countOperationSuccessWithGroupBy sql: {} params: {}", sql, params);
        return jdbcTemplate.query(resultSql.toString(), params.toArray(), new CountExtractor());
    }

    @Override
    public Integer countOperationErrorWithGroupBy(
            String fieldName, Object value, Long from, Long to,
            List<FieldModel> fieldModels, String errorCode) {
        StringBuilder sql = new StringBuilder(String.format(
                "select %1$s, count() as cnt " +
                "from %2$s " +
                "where timestamp >= ? " +
                "and timestamp <= ? " +
                "and eventTime >= ? " +
                "and eventTime <= ? " +
                "and %1$s = ? and status = ? and errorCode = ? ",
                fieldName,
                EventSource.FRAUD_EVENTS_PAYMENT.getTable()
        ));
        StringBuilder sqlGroupBy = new StringBuilder(String.format("group by %1$s", fieldName));
        StringBuilder resultSql = AggregationUtil.appendGroupingFields(fieldModels, sql, sqlGroupBy);
        List<Object> params =
                AggregationUtil.generateParams(from, to, fieldModels, value, PaymentStatus.failed.name(), errorCode);
        log.debug("PaymentRepositoryImpl countOperationErrorWithGroupBy sql: {} params: {}", sql, params);
        return jdbcTemplate.query(resultSql.toString(), params.toArray(), new CountExtractor());
    }

    @Override
    public Long sumOperationSuccessWithGroupBy(
            String fieldName, Object value, Long from, Long to,
            List<FieldModel> fieldModels) {
        StringBuilder sql = new StringBuilder(String.format(
                "select %1$s, sum(amount) as sum " +
                "from %2$s " +
                "where timestamp >= ? " +
                "and timestamp <= ? " +
                "and eventTime >= ? " +
                "and eventTime <= ? " +
                "and %1$s = ? and status = ? ", fieldName, EventSource.FRAUD_EVENTS_PAYMENT.getTable()));
        StringBuilder sqlGroupBy = new StringBuilder(String.format("group by %1$s", fieldName));
        StringBuilder resultSql = AggregationUtil.appendGroupingFields(fieldModels, sql, sqlGroupBy);
        List<Object> params =
                AggregationUtil.generateParams(from, to, fieldModels, value, PaymentStatus.captured.name());
        log.debug("PaymentRepositoryImpl sumOperationSuccessWithGroupBy sql: {} params: {}", sql, params);
        return jdbcTemplate.query(resultSql.toString(), params.toArray(), new SumExtractor());
    }

    @Override
    public Long sumOperationErrorWithGroupBy(
            String fieldName, Object value, Long from, Long to,
            List<FieldModel> fieldModels, String errorCode) {
        StringBuilder sql = new StringBuilder(String.format(
                "select %1$s, sum(amount) as sum " +
                "from %2$s " +
                "where timestamp >= ? " +
                "and timestamp <= ? " +
                "and eventTime >= ? " +
                "and eventTime <= ? " +
                "and %1$s = ? and status = ? and errorCode = ? ",
                fieldName,
                EventSource.FRAUD_EVENTS_PAYMENT.getTable()
        ));
        StringBuilder sqlGroupBy = new StringBuilder(String.format("group by %1$s", fieldName));
        StringBuilder resultSql = AggregationUtil.appendGroupingFields(fieldModels, sql, sqlGroupBy);
        List<Object> params =
                AggregationUtil.generateParams(from, to, fieldModels, value, PaymentStatus.failed.name(), errorCode);
        log.debug("PaymentRepositoryImpl sumOperationErrorWithGroupBy sql: {} params: {}", sql, params);
        return jdbcTemplate.query(resultSql.toString(), params.toArray(), new SumExtractor());
    }

}
