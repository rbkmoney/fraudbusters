package com.rbkmoney.fraudbusters.repository;

import com.google.common.collect.Lists;
import com.rbkmoney.fraudbusters.constant.ClickhouseSchemeNames;
import com.rbkmoney.fraudbusters.domain.Event;
import com.rbkmoney.fraudbusters.fraud.resolver.FieldResolver;
import com.rbkmoney.fraudbusters.repository.extractor.CountExtractor;
import com.rbkmoney.fraudbusters.repository.extractor.SumExtractor;
import com.rbkmoney.fraudbusters.repository.setter.EventBatchPreparedStatementSetter;
import com.rbkmoney.fraudbusters.repository.setter.EventParametersGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.time.ZoneOffset.UTC;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventRepository implements CrudRepository<Event> {

    private final JdbcTemplate jdbcTemplate;

    private static final String INSERT = "INSERT INTO fraud.events_unique " +
            "(timestamp, ip, email, bin, fingerprint, shopId, partyId, resultStatus, amount, eventTime, " +
            "country, checkedRule, bankCountry, currency, invoiceId, maskedPan, bankName, cardToken, paymentId, checkedTemplate)" +
            " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    @Override
    public void insert(Event value) {
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
        if (events != null && !events.isEmpty()) {
            jdbcTemplate.batchUpdate(INSERT, new EventBatchPreparedStatementSetter(events));
        }
    }

    public Integer countOperationByField(String fieldName, String value, Long from, Long to) {
        Date dateFrom = parseDate(from);
        Date dateTo = parseDate(to);

        String sql = String.format("select %1$s, count() as cnt " +
                "from fraud.events_unique " +
                "where (timestamp >= ? " +
                "and timestamp <= ? " +
                "and %1$s = ? " +
                "and eventTime >= ? " +
                "and eventTime <= ?) " +
                "group by %1$s", fieldName);
        return jdbcTemplate.query(sql, new Object[]{dateFrom, dateTo, value, from, to}, new CountExtractor());
    }

    @NotNull
    private Date parseDate(Long to) {
        return Date.valueOf(
                Instant.ofEpochSecond(to)
                        .atZone(UTC)
                        .toLocalDate());
    }

    public Integer countOperationByFieldWithGroupBy(String fieldName, String value, Long from, Long to,
                                                    List<FieldResolver.FieldModel> fieldModels) {
        StringBuilder sql = new StringBuilder(String.format("select %1$s, count() as cnt " +
                "from fraud.events_unique " +
                "where (timestamp >= ? " +
                "and timestamp <= ? " +
                "and %1$s = ? " +
                "and eventTime >= ? " +
                "and eventTime <= ?) ", fieldName));
        StringBuilder sqlGroupBy = new StringBuilder(String.format(" group by %1$s ", fieldName));
        StringBuilder resultSql = appendGroupingFields(fieldModels, sql, sqlGroupBy);
        Date dateFrom = parseDate(from);
        Date dateTo = parseDate(to);
        ArrayList<Object> objects = ParamsUtils.initParams(fieldModels, dateFrom, dateTo, value, from, to);
        return jdbcTemplate.query(resultSql.toString(), objects.toArray(), new CountExtractor());
    }

    private StringBuilder appendGroupingFields(List<FieldResolver.FieldModel> fieldModels, StringBuilder sql, StringBuilder sqlGroupBy) {
        if (fieldModels != null) {
            for (FieldResolver.FieldModel fieldModel : fieldModels) {
                sql.append(" and ").append(fieldModel.getName()).append("=? ");
                sqlGroupBy.append(", ").append(fieldModel.getName());
            }
        }
        return sql.append(sqlGroupBy.toString());
    }

    public Long sumOperationByField(String fieldName, String value, Long from, Long to) {
        String sql = String.format("select %1$s, sum(amount) as sum " +
                "from fraud.events_unique " +
                "where (timestamp >= ? " +
                "and timestamp <= ? " +
                "and %1$s = ? " +
                "and eventTime >= ? " +
                "and eventTime <= ?) " +
                "group by %1$s", fieldName);

        Date dateFrom = parseDate(from);
        Date dateTo = parseDate(to);
        return jdbcTemplate.query(sql, new Object[]{dateFrom, dateTo, value, from, to}, new SumExtractor());
    }

    public Long sumOperationByFieldWithGroupBy(String fieldName, String value, Long from, Long to,
                                               List<FieldResolver.FieldModel> fieldModels) {
        StringBuilder sql = new StringBuilder(String.format("select %1$s, sum(amount) as sum " +
                "from fraud.events_unique " +
                "where (timestamp >= ? " +
                "and timestamp <= ? " +
                "and %1$s = ? " +
                "and eventTime >= ? " +
                "and eventTime <= ?) ", fieldName));
        StringBuilder sqlGroupBy = new StringBuilder(String.format("group by %1$s", fieldName));
        StringBuilder resultSql = appendGroupingFields(fieldModels, sql, sqlGroupBy);

        Date dateFrom = parseDate(from);
        Date dateTo = parseDate(to);
        ArrayList<Object> params = ParamsUtils.initParams(fieldModels, dateFrom, dateTo, value, from, to);
        return jdbcTemplate.query(resultSql.toString(), params.toArray(), new SumExtractor());
    }

    public Integer uniqCountOperation(String fieldNameBy, String value, String fieldNameCount, Long from, Long to) {
        String sql = String.format("select %1$s, uniq(%2$s) as cnt " +
                "from fraud.events_unique " +
                "where (timestamp >= ? " +
                "and timestamp <= ? " +
                "and %1$s = ? " +
                "and eventTime >= ? " +
                "and eventTime <= ?) " +
                "group by %1$s", fieldNameBy, fieldNameCount);

        Date dateFrom = parseDate(from);
        Date dateTo = parseDate(to);
        return jdbcTemplate.query(sql, new Object[]{dateFrom, dateTo, value, from, to}, new CountExtractor());
    }

    public Integer uniqCountOperationWithGroupBy(String fieldNameBy, String value, String fieldNameCount,
                                                 Long from, Long to, List<FieldResolver.FieldModel> fieldModels) {
        StringBuilder sql = new StringBuilder(String.format("select %1$s, uniq(%2$s) as cnt " +
                "from fraud.events_unique " +
                "where (timestamp >= ? " +
                "and timestamp <= ? " +
                "and %1$s = ? " +
                "and eventTime >= ? " +
                "and eventTime <= ?) ", fieldNameBy, fieldNameCount));
        StringBuilder sqlGroupBy = new StringBuilder(String.format("group by %1$s", fieldNameBy));
        StringBuilder resultSql = appendGroupingFields(fieldModels, sql, sqlGroupBy);

        Date dateFrom = parseDate(from);
        Date dateTo = parseDate(to);
        ArrayList<Object> params = ParamsUtils.initParams(fieldModels, dateFrom, dateTo, value, from, to);
        return jdbcTemplate.query(resultSql.toString(), params.toArray(), new CountExtractor());
    }
}
