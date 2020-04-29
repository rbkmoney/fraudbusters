package com.rbkmoney.fraudbusters.repository.impl;

import com.google.common.collect.Lists;
import com.rbkmoney.fraudbusters.constant.ClickhouseSchemeNames;
import com.rbkmoney.fraudbusters.domain.EventP2P;
import com.rbkmoney.fraudbusters.fraud.model.FieldModel;
import com.rbkmoney.fraudbusters.repository.AggregationRepository;
import com.rbkmoney.fraudbusters.repository.CrudRepository;
import com.rbkmoney.fraudbusters.repository.extractor.CountExtractor;
import com.rbkmoney.fraudbusters.repository.extractor.SumExtractor;
import com.rbkmoney.fraudbusters.repository.setter.EventP2PBatchPreparedStatementSetter;
import com.rbkmoney.fraudbusters.repository.setter.EventP2PParametersGenerator;
import com.rbkmoney.fraudbusters.repository.util.ParamsInitiator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.time.ZoneOffset.UTC;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventP2PRepository implements CrudRepository<EventP2P>, AggregationRepository {

    private final JdbcTemplate jdbcTemplate;

    private static final String INSERT = "INSERT INTO fraud.events_p_to_p " +
            "(" +
            "    timestamp,\n" +
            "    eventTime,\n" +
            "    eventTimeHour,\n" +
            "    identityId,\n" +
            "    transferId,\n" +
            "    ip,\n" +
            "    email,\n" +
            "    bin,\n" +
            "    fingerprint,\n" +
            "    amount,\n" +
            "    currency,\n" +
            "    country,\n" +
            "    bankCountry,\n" +
            "    maskedPan,\n" +
            "    bankName,\n" +
            "    cardTokenFrom,\n" +
            "    cardTokenTo,\n" +
            "    resultStatus,\n" +
            "    checkedRule,\n" +
            "    checkedTemplate)" +
            " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    @Override
    public void insert(EventP2P value) {
        if (value != null) {
            Map<String, Object> parameters = EventP2PParametersGenerator.generateParamsByFraudModel(value);
            SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate.getDataSource())
                    .withSchemaName(ClickhouseSchemeNames.FRAUD)
                    .withTableName(ClickhouseSchemeNames.EVENTS_P_TO_P);
            simpleJdbcInsert.setColumnNames(Lists.newArrayList(parameters.keySet()));
            simpleJdbcInsert
                    .execute(parameters);
        }
    }

    @Override
    public void insertBatch(List<EventP2P> events) {
        if (events != null && !events.isEmpty()) {
            jdbcTemplate.batchUpdate(INSERT, new EventP2PBatchPreparedStatementSetter(events));
        }
    }

    @Override
    public Integer countOperationByField(String fieldName, String value, Long from, Long to) {
        Date dateFrom = java.sql.Date.valueOf(
                Instant.ofEpochMilli(from)
                        .atZone(UTC)
                        .toLocalDate());
        Date dateTo = java.sql.Date.valueOf(
                Instant.ofEpochMilli(to)
                        .atZone(UTC)
                        .toLocalDate());

        String sql = String.format("select %1$s, count() as cnt " +
                "from fraud.events_p_to_p " +
                "where (timestamp >= ? " +
                "and timestamp <= ? " +
                "and %1$s = ? " +
                "and eventTime >= ? " +
                "and eventTime <= ?) " +
                "group by %1$s", fieldName);

        return jdbcTemplate.query(
                sql,
                new Object[]{dateFrom, dateTo, value, from, to},
                new CountExtractor()
        );
    }

    @Override
    public Integer countOperationByFieldWithGroupBy(String fieldName, String value, Long from, Long to,
                                                    List<FieldModel> fieldModels) {
        ArrayList<Object> params = generateParams(value, from, to, fieldModels);

        StringBuilder sql = new StringBuilder(String.format("select %1$s, count() as cnt " +
                "from fraud.events_p_to_p " +
                        "where (timestamp >= ? " +
                        "and timestamp <= ? " +
                        "and %1$s = ? " +
                        "and eventTime >= ? " +
                        "and eventTime <= ?) ",
                fieldName));
        StringBuilder sqlGroupBy = new StringBuilder(String.format(" group by %1$s ", fieldName));
        StringBuilder resultSql = appendGroupingFields(fieldModels, sql, sqlGroupBy);

        return jdbcTemplate.query(resultSql.toString(), params.toArray(), new CountExtractor());
    }

    @NotNull
    private ArrayList<Object> generateParams(String value, Long from, Long to, List<FieldModel> fieldModels) {
        Date dateFrom = Date.valueOf(
                Instant.ofEpochMilli(from)
                        .atZone(UTC)
                        .toLocalDate());
        Date dateTo = Date.valueOf(
                Instant.ofEpochMilli(to)
                        .atZone(UTC)
                        .toLocalDate());
        return ParamsInitiator.initParams(fieldModels, dateFrom, dateTo, value, from, to);
    }

    private StringBuilder appendGroupingFields(List<FieldModel> fieldModels, StringBuilder sql, StringBuilder sqlGroupBy) {
        if (fieldModels != null) {
            for (FieldModel fieldModel : fieldModels) {
                sql.append(" and ").append(fieldModel.getName()).append("=? ");
                sqlGroupBy.append(", ").append(fieldModel.getName());
            }
        }
        return sql.append(sqlGroupBy.toString());
    }

    @Override
    public Long sumOperationByFieldWithGroupBy(String fieldName, String value, Long from, Long to,
                                               List<FieldModel> fieldModels) {
        ArrayList<Object> params = generateParams(value, from, to, fieldModels);

        StringBuilder sql = new StringBuilder(String.format("select %1$s, sum(amount) as sum " +
                "from fraud.events_p_to_p " +
                "where (timestamp >= ? " +
                "and timestamp <= ? " +
                "and %1$s = ? " +
                "and eventTime >= ? " +
                "and eventTime <= ?) ", fieldName));
        StringBuilder sqlGroupBy = new StringBuilder(String.format("group by %1$s", fieldName));
        StringBuilder resultSql = appendGroupingFields(fieldModels, sql, sqlGroupBy);

        return jdbcTemplate.query(resultSql.toString(), params.toArray(), new SumExtractor());
    }

    @Override
    public Integer uniqCountOperation(String fieldNameBy, String value, String fieldNameCount, Long from, Long to) {
        String sql = String.format("select %1$s, uniq(%2$s) as cnt " +
                "from fraud.events_p_to_p " +
                "where (timestamp >= ? " +
                "and timestamp <= ? " +
                "and %1$s = ? " +
                "and eventTime >= ? " +
                "and eventTime <= ?) " +
                "group by %1$s", fieldNameBy, fieldNameCount);

        Date dateFrom = java.sql.Date.valueOf(
                Instant.ofEpochMilli(from)
                        .atZone(UTC)
                        .toLocalDate());
        Date dateTo = java.sql.Date.valueOf(
                Instant.ofEpochMilli(to)
                        .atZone(UTC)
                        .toLocalDate());

        return jdbcTemplate.query(sql,
                new Object[]{dateFrom, dateTo, value, from, to},
                new CountExtractor());
    }

    @Override
    public Integer uniqCountOperationWithGroupBy(String fieldNameBy, String value, String fieldNameCount,
                                                 Long from, Long to, List<FieldModel> fieldModels) {
        StringBuilder sql = new StringBuilder(String.format("select %1$s, uniq(%2$s) as cnt " +
                "from fraud.events_p_to_p " +
                "where (timestamp >= ? " +
                "and timestamp <= ? " +
                "and %1$s = ? " +
                "and eventTime >= ? " +
                "and eventTime <= ?) ", fieldNameBy, fieldNameCount));
        StringBuilder sqlGroupBy = new StringBuilder(String.format("group by %1$s", fieldNameBy));
        StringBuilder resultSql = appendGroupingFields(fieldModels, sql, sqlGroupBy);
        ArrayList<Object> params = generateParams(value, from, to, fieldModels);
        return jdbcTemplate.query(resultSql.toString(), params.toArray(), new CountExtractor());
    }
}
