package com.rbkmoney.fraudbusters.repository.impl;

import com.google.common.collect.Lists;
import com.rbkmoney.fraudbusters.constant.AnalyticStatus;
import com.rbkmoney.fraudbusters.constant.EventSource;
import com.rbkmoney.fraudbusters.domain.Payment;
import com.rbkmoney.fraudbusters.fraud.model.FieldModel;
import com.rbkmoney.fraudbusters.repository.AggregationGeneralRepository;
import com.rbkmoney.fraudbusters.repository.PaymentRepository;
import com.rbkmoney.fraudbusters.repository.Repository;
import com.rbkmoney.fraudbusters.repository.extractor.CountExtractor;
import com.rbkmoney.fraudbusters.repository.extractor.SumExtractor;
import com.rbkmoney.fraudbusters.repository.setter.BaseRawParametersGenerator;
import com.rbkmoney.fraudbusters.repository.setter.PaymentBatchPreparedStatementSetter;
import com.rbkmoney.fraudbusters.repository.util.AggregationUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Primary
@Profile("full-prod")
@Component
@RequiredArgsConstructor
public class PaymentRepositoryImpl implements Repository<Payment>, PaymentRepository {

    private final AggregationGeneralRepository aggregationGeneralRepository;
    private final JdbcTemplate jdbcTemplate;

    private static final String INSERT = String.format(
            "INSERT INTO %1s (%2s) VALUES (%3s)",
            EventSource.ANALYTIC_EVENTS_SINK.getTable(),
            BaseRawParametersGenerator.BASE_RAW_PARAMETERS,
            BaseRawParametersGenerator.BASE_RAW_PARAMETERS_MARK);

    @Override
    public void insert(Payment payment) {
        log.debug("PaymentRepository insert payment: {}", payment);
        if (payment != null) {
            Map<String, Object> parameters = BaseRawParametersGenerator.generateParamsByFraudModel(payment);
            SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate.getDataSource())
                    .withTableName(EventSource.ANALYTIC_EVENTS_SINK.getTable());
            simpleJdbcInsert.setColumnNames(Lists.newArrayList(parameters.keySet()));
            simpleJdbcInsert.execute(parameters);
        }
    }

    @Override
    public void insertBatch(List<Payment> batch) {
        log.debug("PaymentRepository insertBatch batch: {}", batch);
        if (batch != null && !batch.isEmpty()) {
            jdbcTemplate.batchUpdate(INSERT, new PaymentBatchPreparedStatementSetter(batch));
        }
    }

    @Override
    public Integer countOperationByField(String fieldName, String value, Long from, Long to) {
        return aggregationGeneralRepository.countOperationByField(EventSource.ANALYTIC_EVENTS_SINK.getTable(), fieldName,
                value, from, to);
    }

    @Override
    public Integer countOperationByFieldWithGroupBy(String fieldName, String value, Long from, Long to, List<FieldModel> fieldModels) {
        return aggregationGeneralRepository.countOperationByFieldWithGroupBy(EventSource.ANALYTIC_EVENTS_SINK.getTable(),
                fieldName, value, from, to, fieldModels);
    }

    @Override
    public Long sumOperationByFieldWithGroupBy(String fieldName, String value, Long from, Long to, List<FieldModel> fieldModels) {
        return aggregationGeneralRepository.sumOperationByFieldWithGroupBy(EventSource.ANALYTIC_EVENTS_SINK.getTable(),
                fieldName, value, from, to, fieldModels);
    }

    @Override
    public Integer uniqCountOperation(String fieldNameBy, String value, String fieldNameCount, Long from, Long to) {
        return aggregationGeneralRepository.uniqCountOperation(EventSource.ANALYTIC_EVENTS_SINK.getTable(), fieldNameBy,
                value, fieldNameCount, from, to);
    }

    @Override
    public Integer uniqCountOperationWithGroupBy(String fieldNameBy, String value, String fieldNameCount, Long from, Long to,
                                                 List<FieldModel> fieldModels) {
        return aggregationGeneralRepository.uniqCountOperationWithGroupBy(EventSource.ANALYTIC_EVENTS_SINK.getTable(),
                fieldNameBy, value, fieldNameCount, from, to, fieldModels);
    }

    @Override
    public Integer countOperationSuccessWithGroupBy(String fieldName, String value, Long from, Long to,
                                                    List<FieldModel> fieldModels) {
        StringBuilder sql = new StringBuilder(String.format(
                "select %1$s, count() as cnt " +
                        "from %2$s " +
                        "where timestamp >= ? " +
                        "and timestamp <= ? " +
                        "and eventTime >= ? " +
                        "and eventTime <= ? " +
                        "and %1$s = ? and status = ? ", fieldName, EventSource.ANALYTIC_EVENTS_SINK.getTable()));
        StringBuilder sqlGroupBy = new StringBuilder(String.format("group by %1$s", fieldName));
        StringBuilder resultSql = AggregationUtil.appendGroupingFields(fieldModels, sql, sqlGroupBy);
        ArrayList<Object> params = AggregationUtil.generateParams(from, to, fieldModels, value, AnalyticStatus.captured.name());
        log.debug("AnalyticRepository countOperationSuccessWithGroupBy sql: {} params: {}", sql, params);
        return jdbcTemplate.query(resultSql.toString(), params.toArray(), new CountExtractor());
    }

    @Override
    public Integer countOperationErrorWithGroupBy(String fieldName, String value, Long from, Long to,
                                                  List<FieldModel> fieldModels, String errorCode) {
        StringBuilder sql = new StringBuilder(String.format(
                "select %1$s, count() as cnt " +
                        "from %2$s " +
                        "where timestamp >= ? " +
                        "and timestamp <= ? " +
                        "and eventTime >= ? " +
                        "and eventTime <= ? " +
                        "and %1$s = ? and status = ? and errorCode=? ", fieldName, EventSource.ANALYTIC_EVENTS_SINK.getTable()));
        StringBuilder sqlGroupBy = new StringBuilder(String.format("group by %1$s", fieldName));
        StringBuilder resultSql = AggregationUtil.appendGroupingFields(fieldModels, sql, sqlGroupBy);
        ArrayList<Object> params = AggregationUtil.generateParams(from, to, fieldModels, value,
                AnalyticStatus.failed.name(), errorCode);
        log.debug("AnalyticRepository countOperationErrorWithGroupBy sql: {} params: {}", sql, params);
        return jdbcTemplate.query(resultSql.toString(), params.toArray(), new CountExtractor());
    }

    @Override
    public Long sumOperationSuccessWithGroupBy(String fieldName, String value, Long from, Long to,
                                               List<FieldModel> fieldModels) {
        StringBuilder sql = new StringBuilder(String.format(
                "select %1$s, sum(amount) as sum " +
                        "from %2$s " +
                        "where timestamp >= ? " +
                        "and timestamp <= ? " +
                        "and eventTime >= ? " +
                        "and eventTime <= ? " +
                        "and %1$s = ? and status = ? ", fieldName, EventSource.ANALYTIC_EVENTS_SINK.getTable()));
        StringBuilder sqlGroupBy = new StringBuilder(String.format("group by %1$s", fieldName));
        StringBuilder resultSql = AggregationUtil.appendGroupingFields(fieldModels, sql, sqlGroupBy);
        ArrayList<Object> params = AggregationUtil.generateParams(from, to, fieldModels, value, AnalyticStatus.captured.name());
        log.debug("AnalyticRepository sumOperationSuccessWithGroupBy sql: {} params: {}", sql, params);
        return jdbcTemplate.query(resultSql.toString(), params.toArray(), new SumExtractor());
    }

    @Override
    public Long sumOperationErrorWithGroupBy(String fieldName, String value, Long from, Long to,
                                             List<FieldModel> fieldModels, String errorCode) {
        StringBuilder sql = new StringBuilder(String.format(
                "select %1$s, sum(amount) as sum " +
                        "from %2$s " +
                        "where timestamp >= ? " +
                        "and timestamp <= ? " +
                        "and eventTime >= ? " +
                        "and eventTime <= ? " +
                        "and %1$s = ? and status = ? and errorCode=? ", fieldName, EventSource.ANALYTIC_EVENTS_SINK.getTable()));
        StringBuilder sqlGroupBy = new StringBuilder(String.format("group by %1$s", fieldName));
        StringBuilder resultSql = AggregationUtil.appendGroupingFields(fieldModels, sql, sqlGroupBy);
        ArrayList<Object> params = AggregationUtil.generateParams(from, to, fieldModels, value,
                AnalyticStatus.failed.name(), errorCode);
        log.debug("AnalyticRepository sumOperationErrorWithGroupBy sql: {} params: {}", sql, params);
        return jdbcTemplate.query(resultSql.toString(), params.toArray(), new SumExtractor());
    }
}
