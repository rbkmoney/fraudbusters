package com.rbkmoney.fraudbusters.repository.impl;

import com.rbkmoney.fraudbusters.constant.AnalyticStatus;
import com.rbkmoney.fraudbusters.constant.EventSource;
import com.rbkmoney.fraudbusters.fraud.model.FieldModel;
import com.rbkmoney.fraudbusters.repository.AggregationGeneralRepository;
import com.rbkmoney.fraudbusters.repository.AggregationRepository;
import com.rbkmoney.fraudbusters.repository.extractor.CountExtractor;
import com.rbkmoney.fraudbusters.repository.extractor.SumExtractor;
import com.rbkmoney.fraudbusters.repository.util.AggregationUtil;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@Order(1)
@RequiredArgsConstructor
public class AnalyticRepository implements AggregationRepository {

    @Getter
    private final EventSource eventSource = EventSource.ANALYTIC_EVENTS_SINK;

    private final AggregationGeneralRepository aggregationGeneralRepository;
    private final JdbcTemplate jdbcTemplate;

    @Override
    public Integer countOperationByField(String fieldName, String value, Long from, Long to) {
        return aggregationGeneralRepository.countOperationByField(eventSource.getTable(), fieldName, value, from, to);
    }

    @Override
    public Integer countOperationByFieldWithGroupBy(String fieldName, String value, Long from, Long to, List<FieldModel> fieldModels) {
        return aggregationGeneralRepository.countOperationByFieldWithGroupBy(eventSource.getTable(), fieldName, value, from, to, fieldModels);
    }

    @Override
    public Long sumOperationByFieldWithGroupBy(String fieldName, String value, Long from, Long to, List<FieldModel> fieldModels) {
        return aggregationGeneralRepository.sumOperationByFieldWithGroupBy(eventSource.getTable(), fieldName, value, from, to, fieldModels);
    }

    @Override
    public Integer uniqCountOperation(String fieldNameBy, String value, String fieldNameCount, Long from, Long to) {
        return aggregationGeneralRepository.uniqCountOperation(eventSource.getTable(), fieldNameBy, value, fieldNameCount, from, to);
    }

    @Override
    public Integer uniqCountOperationWithGroupBy(String fieldNameBy, String value, String fieldNameCount, Long from, Long to, List<FieldModel> fieldModels) {
        return aggregationGeneralRepository.uniqCountOperationWithGroupBy(eventSource.getTable(), fieldNameBy, value, fieldNameCount, from, to, fieldModels);
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
                        "and %1$s = ? and status = ? ", fieldName, eventSource.getTable()));
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
                        "and %1$s = ? and status = ? and errorCode=? ", fieldName, eventSource.getTable()));
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
                        "and %1$s = ? and status = ? ", fieldName, eventSource.getTable()));
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
                        "and %1$s = ? and status = ? and errorCode=? ", fieldName, eventSource.getTable(), errorCode));
        StringBuilder sqlGroupBy = new StringBuilder(String.format("group by %1$s", fieldName));
        StringBuilder resultSql = AggregationUtil.appendGroupingFields(fieldModels, sql, sqlGroupBy);
        ArrayList<Object> params = AggregationUtil.generateParams(from, to, fieldModels, value,
                AnalyticStatus.failed.name(), errorCode);
        log.debug("AnalyticRepository sumOperationErrorWithGroupBy sql: {} params: {}", sql, params);
        return jdbcTemplate.query(resultSql.toString(), params.toArray(), new SumExtractor());
    }

}
