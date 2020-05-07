package com.rbkmoney.fraudbusters.repository.impl;

import com.google.common.collect.Lists;
import com.rbkmoney.fraudbusters.constant.ClickhouseSchemeNames;
import com.rbkmoney.fraudbusters.constant.EventSource;
import com.rbkmoney.fraudbusters.domain.Event;
import com.rbkmoney.fraudbusters.fraud.model.FieldModel;
import com.rbkmoney.fraudbusters.repository.AggregationGeneralRepository;
import com.rbkmoney.fraudbusters.repository.AggregationRepository;
import com.rbkmoney.fraudbusters.repository.CrudRepository;
import com.rbkmoney.fraudbusters.repository.extractor.CountExtractor;
import com.rbkmoney.fraudbusters.repository.extractor.SumExtractor;
import com.rbkmoney.fraudbusters.repository.setter.EventBatchPreparedStatementSetter;
import com.rbkmoney.fraudbusters.repository.setter.EventParametersGenerator;
import com.rbkmoney.fraudbusters.repository.util.AggregationUtil;
import com.rbkmoney.fraudo.constant.ResultStatus;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@Order(2)
@RequiredArgsConstructor
public class FraudResultRepository implements CrudRepository<Event>, AggregationRepository {

    @Getter
    private final EventSource eventSource = EventSource.FRAUD_EVENTS_UNIQUE;

    private final AggregationGeneralRepository aggregationGeneralRepository;
    private final JdbcTemplate jdbcTemplate;

    private static final String INSERT = "INSERT INTO fraud.events_unique " +
            " (timestamp, eventTimeHour, eventTime, ip, email, bin, fingerprint, shopId, partyId, resultStatus, amount, " +
            "country, checkedRule, bankCountry, currency, invoiceId, maskedPan, bankName, cardToken, paymentId, checkedTemplate)" +
            " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    @Override
    public void insert(Event value) {
        log.debug("EventRepository insertBatch value: {}", value);
        if (value != null) {
            Map<String, Object> parameters = EventParametersGenerator.generateParamsByFraudModel(value);
            SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate.getDataSource())
                    .withSchemaName(ClickhouseSchemeNames.FRAUD)
                    .withTableName(ClickhouseSchemeNames.EVENTS_UNIQUE);
            simpleJdbcInsert.setColumnNames(Lists.newArrayList(parameters.keySet()));
            simpleJdbcInsert
                    .execute(parameters);
        }
    }

    @Override
    public void insertBatch(List<Event> events) {
        log.debug("EventRepository insertBatch events: {}", events);
        if (events != null && !events.isEmpty()) {
            jdbcTemplate.batchUpdate(INSERT, new EventBatchPreparedStatementSetter(events));
        }
    }

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
        StringBuilder sql = new StringBuilder(String.format("select %1$s, count() as cnt " +
                "from %2$s " +
                "where timestamp >= ? " +
                "and timestamp <= ? " +
                "and eventTime >= ? " +
                "and eventTime <= ? " +
                "and %1$s = ? and resultStatus != ? ", fieldName, eventSource.getTable()));
        StringBuilder sqlGroupBy = new StringBuilder(String.format("group by %1$s", fieldName));
        StringBuilder resultSql = AggregationUtil.appendGroupingFields(fieldModels, sql, sqlGroupBy);
        ArrayList<Object> params = AggregationUtil.generateParams(from, to, fieldModels, value, ResultStatus.DECLINE.name());
        log.debug("FraudResultRepository countOperationSuccessWithGroupBy sql: {} params: {}", sql, params);
        return jdbcTemplate.query(resultSql.toString(), params.toArray(), new CountExtractor());
    }

    @Override
    public Integer countOperationErrorWithGroupBy(String fieldName, String value, Long from, Long to,
                                                  List<FieldModel> fieldModels, String errorCode) {
        log.warn("Error code ignore on this source: {} errorCode: {}", eventSource.getTable(), errorCode);
        StringBuilder sql = new StringBuilder(String.format("select %1$s, count() as cnt " +
                "from %2$s " +
                "where timestamp >= ? " +
                "and timestamp <= ? " +
                "and eventTime >= ? " +
                "and eventTime <= ? " +
                "and %1$s = ? and resultStatus = ? ", fieldName, eventSource.getTable()));
        StringBuilder sqlGroupBy = new StringBuilder(String.format("group by %1$s", fieldName));
        StringBuilder resultSql = AggregationUtil.appendGroupingFields(fieldModels, sql, sqlGroupBy);
        ArrayList<Object> params = AggregationUtil.generateParams(from, to, fieldModels, value, ResultStatus.DECLINE.name());
        log.debug("FraudResultRepository countOperationErrorWithGroupBy sql: {} params: {}", sql, params);
        return jdbcTemplate.query(resultSql.toString(), params.toArray(), new CountExtractor());
    }

    @Override
    public Long sumOperationSuccessWithGroupBy(String fieldName, String value, Long from, Long to,
                                               List<FieldModel> fieldModels) {
        StringBuilder sql = new StringBuilder(String.format("select %1$s, sum(amount) as sum " +
                "from %2$s " +
                "where timestamp >= ? " +
                "and timestamp <= ? " +
                "and eventTime >= ? " +
                "and eventTime <= ? " +
                "and %1$s = ? and resultStatus != ? ", fieldName, eventSource.getTable()));
        StringBuilder sqlGroupBy = new StringBuilder(String.format("group by %1$s", fieldName));
        StringBuilder resultSql = AggregationUtil.appendGroupingFields(fieldModels, sql, sqlGroupBy);
        ArrayList<Object> params = AggregationUtil.generateParams(from, to, fieldModels, value, ResultStatus.DECLINE.name());
        log.debug("FraudResultRepository sumOperationSuccessWithGroupBy sql: {} params: {}", sql, params);
        return jdbcTemplate.query(resultSql.toString(), params.toArray(), new SumExtractor());
    }

    @Override
    public Long sumOperationErrorWithGroupBy(String fieldName, String value, Long from, Long to,
                                             List<FieldModel> fieldModels, String errorCode) {
        log.warn("Error code ignore on this source: {} errorCode: {}", eventSource.getTable(), errorCode);
        StringBuilder sql = new StringBuilder(String.format("select %1$s, sum(amount) as sum " +
                "from %2$s " +
                "where timestamp >= ? " +
                "and timestamp <= ? " +
                "and eventTime >= ? " +
                "and eventTime <= ? " +
                "and %1$s = ? and resultStatus = ? ", fieldName, eventSource.getTable()));
        StringBuilder sqlGroupBy = new StringBuilder(String.format("group by %1$s", fieldName));
        StringBuilder resultSql = AggregationUtil.appendGroupingFields(fieldModels, sql, sqlGroupBy);
        ArrayList<Object> params = AggregationUtil.generateParams(from, to, fieldModels, value, ResultStatus.DECLINE.name());
        log.debug("FraudResultRepository sumOperationErrorWithGroupBy sql: {} params: {}", sql, params);
        return jdbcTemplate.query(resultSql.toString(), params.toArray(), new SumExtractor());
    }

}
