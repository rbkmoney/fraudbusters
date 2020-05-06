package com.rbkmoney.fraudbusters.repository.impl;

import com.rbkmoney.fraudbusters.fraud.model.FieldModel;
import com.rbkmoney.fraudbusters.repository.AggregationGeneralRepository;
import com.rbkmoney.fraudbusters.repository.extractor.CountExtractor;
import com.rbkmoney.fraudbusters.repository.extractor.SumExtractor;
import com.rbkmoney.fraudbusters.repository.util.ParamsInitiator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static java.time.ZoneOffset.UTC;

@Slf4j
@Service
@RequiredArgsConstructor
public class AggregationGeneralRepositoryImpl implements AggregationGeneralRepository {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Integer countOperationByField(String table, String fieldName, String value, Long from, Long to) {
        Instant instantFrom = Instant.ofEpochMilli(from);
        LocalDate dateFrom = instantFrom.atZone(UTC).toLocalDate();
        Instant instantTo = Instant.ofEpochMilli(to);
        LocalDate dateTo = instantTo.atZone(UTC).toLocalDate();

        String sql = String.format(
                "select %1$s, count() as cnt " +
                        "from %2$s " +
                        "where timestamp >= ? " +
                        "and timestamp <= ? " +
                        "and eventTime >= ? " +
                        "and eventTime <= ? " +
                        "and %1$s = ? " +
                        "group by %1$s", fieldName, table);

        Object[] params = {dateFrom, dateTo, instantFrom.getEpochSecond(), instantTo.getEpochSecond(), value};
        log.debug("AggregationGeneralRepositoryImpl countOperationByField sql: {} params: {}", sql, params);
        return jdbcTemplate.query(sql, params, new CountExtractor());
    }

    @Override
    public Integer countOperationByFieldWithGroupBy(String table, String fieldName, String value, Long from, Long to,
                                                    List<FieldModel> fieldModels) {
        ArrayList<Object> params = generateParams(value, from, to, fieldModels);

        StringBuilder sql = new StringBuilder(String.format(
                "select %1$s, count() as cnt " +
                        "from %2$s " +
                        "where timestamp >= ? " +
                        "and timestamp <= ? " +
                        "and eventTime >= ? " +
                        "and eventTime <= ? " +
                        "and %1$s = ? ", fieldName, table));
        StringBuilder sqlGroupBy = new StringBuilder(String.format(" group by %1$s ", fieldName));
        StringBuilder resultSql = appendGroupingFields(fieldModels, sql, sqlGroupBy);
        String sqlResult = resultSql.toString();
        log.debug("AggregationGeneralRepositoryImpl countOperationByFieldWithGroupBy sql: {} params: {}", sqlResult, params);
        return jdbcTemplate.query(sqlResult, params.toArray(), new CountExtractor());
    }

    @NotNull
    private ArrayList<Object> generateParams(String value, Long from, Long to, List<FieldModel> fieldModels) {
        Instant instantFrom = Instant.ofEpochMilli(from);
        LocalDate dateFrom = instantFrom.atZone(UTC).toLocalDate();
        Instant instantTo = Instant.ofEpochMilli(to);
        LocalDate dateTo = instantTo.atZone(UTC).toLocalDate();
        return ParamsInitiator.initParams(fieldModels, dateFrom, dateTo,
                instantFrom.getEpochSecond(), instantTo.getEpochSecond(), value);
    }

    @Override
    public Long sumOperationByFieldWithGroupBy(String table, String fieldName, String value, Long from, Long to,
                                               List<FieldModel> fieldModels) {
        ArrayList<Object> params = generateParams(value, from, to, fieldModels);

        StringBuilder sql = new StringBuilder(String.format(
                "select %1$s, sum(amount) as sum " +
                        "from %2$s " +
                        "where timestamp >= ? " +
                        "and timestamp <= ? " +
                        "and eventTime >= ? " +
                        "and eventTime <= ? " +
                        "and %1$s = ? ", fieldName, table));
        StringBuilder sqlGroupBy = new StringBuilder(String.format("group by %1$s", fieldName));
        StringBuilder resultSql = appendGroupingFields(fieldModels, sql, sqlGroupBy);

        String sqlResult = resultSql.toString();
        log.debug("AggregationGeneralRepositoryImpl sumOperationByFieldWithGroupBy sql: {} params: {}", sqlResult, params);
        return jdbcTemplate.query(sqlResult, params.toArray(), new SumExtractor());
    }

    @Override
    public Integer uniqCountOperation(String table, String fieldNameBy, String value, String fieldNameCount, Long from, Long to) {
        String sql = String.format(
                "select %1$s, uniq(%2$s) as cnt " +
                        "from %3$s " +
                        "where timestamp >= ? " +
                        "and timestamp <= ? " +
                        "and eventTime >= ? " +
                        "and eventTime <= ? " +
                        "and %1$s = ? " +
                        "group by %1$s", fieldNameBy, fieldNameCount, table);

        Instant instantFrom = Instant.ofEpochMilli(from);
        LocalDate dateFrom = instantFrom.atZone(UTC).toLocalDate();
        Instant instantTo = Instant.ofEpochMilli(to);
        LocalDate dateTo = instantTo.atZone(UTC).toLocalDate();

        Object[] params = {dateFrom, dateTo, instantFrom.getEpochSecond(), instantTo.getEpochSecond(), value};
        log.debug("AggregationGeneralRepositoryImpl uniqCountOperation sql: {} params: {}", sql, params);
        return jdbcTemplate.query(sql, params, new CountExtractor());
    }

    @Override
    public Integer uniqCountOperationWithGroupBy(String table, String fieldNameBy, String value, String fieldNameCount,
                                                 Long from, Long to, List<FieldModel> fieldModels) {
        StringBuilder sql = new StringBuilder(String.format(
                "select %1$s, uniq(%2$s) as cnt " +
                        "from %3$s " +
                        "where timestamp >= ? " +
                        "and timestamp <= ? " +
                        "and eventTime >= ? " +
                        "and eventTime <= ? " +
                        "and %1$s = ? ", fieldNameBy, fieldNameCount, table));
        StringBuilder sqlGroupBy = new StringBuilder(String.format("group by %1$s", fieldNameBy));
        StringBuilder resultSql = appendGroupingFields(fieldModels, sql, sqlGroupBy);
        ArrayList<Object> params = generateParams(value, from, to, fieldModels);
        String sqlResult = resultSql.toString();
        log.debug("AggregationGeneralRepositoryImpl uniqCountOperationWithGroupBy sql: {} params: {}", sqlResult, params);
        return jdbcTemplate.query(sqlResult, params.toArray(), new CountExtractor());
    }

}
