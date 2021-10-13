package com.rbkmoney.fraudbusters.repository.clickhouse.impl;

import com.rbkmoney.fraudbusters.fraud.model.FieldModel;
import com.rbkmoney.fraudbusters.repository.AggregationStatusGeneralRepository;
import com.rbkmoney.fraudbusters.repository.clickhouse.extractor.CountExtractor;
import com.rbkmoney.fraudbusters.repository.clickhouse.extractor.SumExtractor;
import com.rbkmoney.fraudbusters.repository.clickhouse.util.AggregationUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AggregationStatusGeneralRepositoryImpl implements AggregationStatusGeneralRepository {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Integer countOperationByField(
            String table,
            String fieldName,
            Object value,
            Long from,
            Long to,
            String status) {
        String sql = String.format("""
                select %1$s, count() as cnt
                from %2$s
                where timestamp >= ?
                and timestamp <= ?
                and eventTime >= ?
                and eventTime <= ?
                and %1$s = ? and status = ?
                group by %1$s""", fieldName, table);
        List<Object> params = AggregationUtil.generateStatusParams(from, to, value, status);
        log.debug("AggregationGeneralRepositoryImpl countOperationByField sql: {} params: {}", sql, params);
        return jdbcTemplate.query(sql, params.toArray(), new CountExtractor());
    }

    @Override
    public Integer countOperationByFieldWithGroupBy(
            String table, String fieldName, Object value, Long from, Long to,
            List<FieldModel> fieldModels, String status) {
        List<Object> params = AggregationUtil.generateParams(from, to, fieldModels, value, status);

        StringBuilder sql = new StringBuilder(String.format("""
                select %1$s, count() as cnt
                from %2$s
                where timestamp >= ?
                and timestamp <= ?
                and eventTime >= ?
                and eventTime <= ?
                and %1$s = ? and status = ?""", fieldName, table));
        StringBuilder sqlGroupBy = new StringBuilder(String.format(" group by %1$s ", fieldName));
        StringBuilder resultSql = AggregationUtil.appendGroupingFields(fieldModels, sql, sqlGroupBy);
        String sqlResult = resultSql.toString();
        log.debug(
                "AggregationGeneralRepositoryImpl countOperationByFieldWithGroupBy sql: {} params: {}",
                sqlResult,
                params
        );
        return jdbcTemplate.query(sqlResult, params.toArray(), new CountExtractor());
    }

    @Override
    public Long sumOperationByFieldWithGroupBy(
            String table, String fieldName, Object value, Long from, Long to,
            List<FieldModel> fieldModels, String status) {
        List<Object> params = AggregationUtil.generateParams(from, to, fieldModels, value, status);

        StringBuilder sql = new StringBuilder(String.format("""
                select %1$s, sum(amount) as sum
                from %2$s
                where timestamp >= ?
                and timestamp <= ?
                and eventTime >= ?
                and eventTime <= ?
                and %1$s = ? and status = ?""", fieldName, table));
        StringBuilder sqlGroupBy = new StringBuilder(String.format("group by %1$s", fieldName));
        StringBuilder resultSql = AggregationUtil.appendGroupingFields(fieldModels, sql, sqlGroupBy);

        String sqlResult = resultSql.toString();
        log.debug(
                "AggregationGeneralRepositoryImpl sumOperationByFieldWithGroupBy sql: {} params: {}",
                sqlResult,
                params
        );
        return jdbcTemplate.query(sqlResult, params.toArray(), new SumExtractor());
    }

    @Override
    public Integer uniqCountOperation(
            String table, String fieldNameBy, Object value, String fieldNameCount,
            Long from, Long to, String status) {
        String sql = String.format("""
                select %1$s, uniq(%2$s) as cnt
                from %3$s
                where timestamp >= ?
                and timestamp <= ?
                and eventTime >= ?
                and eventTime <= ?
                and %1$s = ? and status = ?
                group by %1$s""", fieldNameBy, fieldNameCount, table);
        List<Object> params = AggregationUtil.generateStatusParams(from, to, value, status);
        log.debug("AggregationGeneralRepositoryImpl uniqCountOperation sql: {} params: {}", sql, params);
        return jdbcTemplate.query(sql, params.toArray(), new CountExtractor());
    }

    @Override
    public Integer uniqCountOperationWithGroupBy(
            String table, String fieldNameBy, Object value, String fieldNameCount,
            Long from, Long to, List<FieldModel> fieldModels, String status) {
        StringBuilder sql = new StringBuilder(String.format("""
                select %1$s, uniq(%2$s) as cnt
                from %3$s
                where timestamp >= ?
                and timestamp <= ?
                and eventTime >= ?
                and eventTime <= ?
                and %1$s = ? and status = ?""", fieldNameBy, fieldNameCount, table));
        StringBuilder sqlGroupBy = new StringBuilder(String.format("group by %1$s", fieldNameBy));
        StringBuilder resultSql = AggregationUtil.appendGroupingFields(fieldModels, sql, sqlGroupBy);
        List<Object> params = AggregationUtil.generateParams(from, to, fieldModels, value, status);
        String sqlResult = resultSql.toString();
        log.debug(
                "AggregationGeneralRepositoryImpl uniqCountOperationWithGroupBy sql: {} params: {}",
                sqlResult,
                params
        );
        return jdbcTemplate.query(sqlResult, params.toArray(), new CountExtractor());
    }

}
