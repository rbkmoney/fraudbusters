package com.rbkmoney.fraudbusters.repository.impl;

import com.rbkmoney.fraudbusters.constant.AnalyticStatus;
import com.rbkmoney.fraudbusters.constant.EventSource;
import com.rbkmoney.fraudbusters.domain.CheckedPayment;
import com.rbkmoney.fraudbusters.fraud.model.FieldModel;
import com.rbkmoney.fraudbusters.repository.AggregationGeneralRepository;
import com.rbkmoney.fraudbusters.repository.PaymentRepository;
import com.rbkmoney.fraudbusters.repository.Repository;
import com.rbkmoney.fraudbusters.repository.extractor.CountExtractor;
import com.rbkmoney.fraudbusters.repository.extractor.SumExtractor;
import com.rbkmoney.fraudbusters.repository.setter.PaymentBatchPreparedStatementSetter;
import com.rbkmoney.fraudbusters.repository.util.AggregationUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentRepositoryImpl implements Repository<CheckedPayment>, PaymentRepository {

    private final AggregationGeneralRepository aggregationGeneralRepository;
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


    @Override
    public Integer countOperationByField(String fieldName, String value, Long from, Long to) {
        return aggregationGeneralRepository.countOperationByField(EventSource.FRAUD_EVENTS_PAYMENT.getTable(), fieldName,
                value, from, to);
    }

    @Override
    public Integer countOperationByFieldWithGroupBy(String fieldName, String value, Long from, Long to, List<FieldModel> fieldModels) {
        return aggregationGeneralRepository.countOperationByFieldWithGroupBy(EventSource.FRAUD_EVENTS_PAYMENT.getTable(),
                fieldName, value, from, to, fieldModels);
    }

    @Override
    public Long sumOperationByFieldWithGroupBy(String fieldName, String value, Long from, Long to, List<FieldModel> fieldModels) {
        return aggregationGeneralRepository.sumOperationByFieldWithGroupBy(EventSource.FRAUD_EVENTS_PAYMENT.getTable(),
                fieldName, value, from, to, fieldModels);
    }

    @Override
    public Integer uniqCountOperation(String fieldNameBy, String value, String fieldNameCount, Long from, Long to) {
        return aggregationGeneralRepository.uniqCountOperation(EventSource.FRAUD_EVENTS_PAYMENT.getTable(), fieldNameBy,
                value, fieldNameCount, from, to);
    }

    @Override
    public Integer uniqCountOperationWithGroupBy(String fieldNameBy, String value, String fieldNameCount, Long from, Long to,
                                                 List<FieldModel> fieldModels) {
        return aggregationGeneralRepository.uniqCountOperationWithGroupBy(EventSource.FRAUD_EVENTS_PAYMENT.getTable(),
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
                        "and %1$s = ? and status = ? ", fieldName, EventSource.FRAUD_EVENTS_PAYMENT.getTable()));
        StringBuilder sqlGroupBy = new StringBuilder(String.format("group by %1$s", fieldName));
        StringBuilder resultSql = AggregationUtil.appendGroupingFields(fieldModels, sql, sqlGroupBy);
        ArrayList<Object> params = AggregationUtil.generateParams(from, to, fieldModels, value, AnalyticStatus.captured.name());
        log.debug("PaymentRepositoryImpl countOperationSuccessWithGroupBy sql: {} params: {}", sql, params);
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
                        "and %1$s = ? and status = ? and errorCode=? ", fieldName, EventSource.FRAUD_EVENTS_PAYMENT.getTable()));
        StringBuilder sqlGroupBy = new StringBuilder(String.format("group by %1$s", fieldName));
        StringBuilder resultSql = AggregationUtil.appendGroupingFields(fieldModels, sql, sqlGroupBy);
        ArrayList<Object> params = AggregationUtil.generateParams(from, to, fieldModels, value,
                AnalyticStatus.failed.name(), errorCode);
        log.debug("PaymentRepositoryImpl countOperationErrorWithGroupBy sql: {} params: {}", sql, params);
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
                        "and %1$s = ? and status = ? ", fieldName, EventSource.FRAUD_EVENTS_PAYMENT.getTable()));
        StringBuilder sqlGroupBy = new StringBuilder(String.format("group by %1$s", fieldName));
        StringBuilder resultSql = AggregationUtil.appendGroupingFields(fieldModels, sql, sqlGroupBy);
        ArrayList<Object> params = AggregationUtil.generateParams(from, to, fieldModels, value, AnalyticStatus.captured.name());
        log.debug("PaymentRepositoryImpl sumOperationSuccessWithGroupBy sql: {} params: {}", sql, params);
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
                        "and %1$s = ? and status = ? and errorCode=? ", fieldName, EventSource.FRAUD_EVENTS_PAYMENT.getTable()));
        StringBuilder sqlGroupBy = new StringBuilder(String.format("group by %1$s", fieldName));
        StringBuilder resultSql = AggregationUtil.appendGroupingFields(fieldModels, sql, sqlGroupBy);
        ArrayList<Object> params = AggregationUtil.generateParams(from, to, fieldModels, value,
                AnalyticStatus.failed.name(), errorCode);
        log.debug("PaymentRepositoryImpl sumOperationErrorWithGroupBy sql: {} params: {}", sql, params);
        return jdbcTemplate.query(resultSql.toString(), params.toArray(), new SumExtractor());
    }
}
