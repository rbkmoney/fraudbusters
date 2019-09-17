package com.rbkmoney.fraudbusters.repository;

import com.google.common.collect.Lists;
import com.rbkmoney.fraudbusters.constant.EventField;
import com.rbkmoney.fraudbusters.domain.Event;
import com.rbkmoney.fraudbusters.fraud.resolver.FieldResolver;
import com.rbkmoney.fraudbusters.repository.extractor.CountExtractor;
import com.rbkmoney.fraudbusters.repository.extractor.SumExtractor;
import com.rbkmoney.fraudbusters.repository.setter.EventBatchPreparedStatementSetter;
import com.rbkmoney.fraudbusters.repository.setter.EventParametersGenerator;
import com.rbkmoney.fraudo.constant.ResultStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventSinkMgRepository implements CrudRepository<Event> {

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
                    .withSchemaName("fraud")
                    .withTableName("events_unique");
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

    public Integer countOperationByField(EventField fieldName, String value, Long from, Long to) {
        String sql = String.format("select %1$s, count() as cnt " +
                "from fraud.events_unique " +
                "where (eventTime >= ? and eventTime <= ? and %1$s = ?)" +
                "group by %1$s", fieldName.name());
        return jdbcTemplate.query(sql, new Object[]{from, to, value}, new CountExtractor());
    }

    public Integer countOperationByFieldWithGroupBy(EventField fieldName, String value, Long from, Long to,
                                                    List<FieldResolver.FieldModel> fieldModels) {
        StringBuilder sql = new StringBuilder(String.format("select %1$s, count() as cnt " +
                "from fraud.events_unique " +
                "where eventTime >= ? and eventTime <= ? and %1$s = ? ", fieldName.name()));
        StringBuilder sqlGroupBy = new StringBuilder(String.format(" group by %1$s ", fieldName.name()));
        StringBuilder resultSql = appendGroupingFields(fieldModels, sql, sqlGroupBy);
        ArrayList<Object> objects = ParamsUtils.initParams(fieldModels, from, to, value);
        return jdbcTemplate.query(resultSql.toString(), objects.toArray(), new CountExtractor());
    }

    private StringBuilder appendGroupingFields(List<FieldResolver.FieldModel> fieldModels, StringBuilder sql, StringBuilder sqlGroupBy) {
        if (fieldModels != null) {
            for (FieldResolver.FieldModel fieldModel : fieldModels) {
                sql.append(" and ").append(fieldModel.getName().name()).append("=? ");
                sqlGroupBy.append(", ").append(fieldModel.getName().name());
            }
        }
        return sql.append(sqlGroupBy.toString());
    }

    public Integer countOperationSuccess(EventField fieldName, String value, Long from, Long to) {
        String sql = String.format("select %1$s, count() as cnt " +
                "from fraud.events_unique " +
                "where (eventTime >= ? and eventTime <= ? and %1$s = ? and resultStatus in (?, ?, ?))" +
                "group by %1$s", fieldName.name());
        return jdbcTemplate.query(sql, new Object[]{from, to, value, ResultStatus.ACCEPT.toString(),
                ResultStatus.NORMAL.toString(), ResultStatus.THREE_DS.toString()}, new CountExtractor());
    }

    public Integer countOperationSuccessWithGroupBy(EventField fieldName, String value, Long from, Long to,
                                                    List<FieldResolver.FieldModel> fieldModels) {
        StringBuilder sql = new StringBuilder(String.format("select %1$s, count() as cnt " +
                "from fraud.events_unique " +
                "where eventTime >= ? and eventTime <= ? and %1$s = ? and resultStatus in (?, ?, ?) ", fieldName.name()));
        StringBuilder sqlGroupBy = new StringBuilder(String.format("group by %1$s", fieldName.name()));
        StringBuilder resultSql = appendGroupingFields(fieldModels, sql, sqlGroupBy);
        ArrayList<Object> params = ParamsUtils.initParams(fieldModels, from, to, value, ResultStatus.ACCEPT.toString(),
                ResultStatus.NORMAL.toString(), ResultStatus.THREE_DS.toString());
        return jdbcTemplate.query(resultSql.toString(), params.toArray(), new CountExtractor());
    }

    public Integer countOperationError(EventField fieldName, String value, Long from, Long to) {
        String sql = String.format("select %1$s, count() as cnt " +
                "from fraud.events_unique " +
                "where (eventTime >= ? and eventTime <= ? and %1$s = ? and resultStatus = ?)" +
                "group by %1$s", fieldName.name());
        return jdbcTemplate.query(sql, new Object[]{from, to, value, ResultStatus.DECLINE.toString()}, new CountExtractor());
    }

    public Integer countOperationErrorWithGroupBy(EventField fieldName, String value, Long from, Long to,
                                                  List<FieldResolver.FieldModel> fieldModels) {
        StringBuilder sql = new StringBuilder(String.format("select %1$s, count() as cnt " +
                "from fraud.events_unique " +
                "where eventTime >= ? and eventTime <= ? and %1$s = ? and resultStatus = ? ", fieldName.name()));
        StringBuilder sqlGroupBy = new StringBuilder(String.format("group by %1$s", fieldName.name()));
        StringBuilder resultSql = appendGroupingFields(fieldModels, sql, sqlGroupBy);
        ArrayList<Object> params = ParamsUtils.initParams(fieldModels, from, to, value, ResultStatus.DECLINE.toString());
        return jdbcTemplate.query(resultSql.toString(), params.toArray(), new CountExtractor());
    }

    public Long sumOperationByField(EventField fieldName, String value, Long from, Long to) {
        String sql = String.format("select %1$s, sum(amount) as sum " +
                "from fraud.events_unique " +
                "where (eventTime >= ? and eventTime <= ? and %1$s = ?)" +
                "group by %1$s", fieldName.name());
        return jdbcTemplate.query(sql, new Object[]{from, to, value}, new SumExtractor());
    }

    public Long sumOperationByFieldWithGroupBy(EventField fieldName, String value, Long from, Long to,
                                               List<FieldResolver.FieldModel> fieldModels) {
        StringBuilder sql = new StringBuilder(String.format("select %1$s, sum(amount) as sum " +
                "from fraud.events_unique " +
                "where eventTime >= ? and eventTime <= ? and %1$s = ? ", fieldName.name()));
        StringBuilder sqlGroupBy = new StringBuilder(String.format("group by %1$s", fieldName.name()));
        StringBuilder resultSql = appendGroupingFields(fieldModels, sql, sqlGroupBy);
        ArrayList<Object> params = ParamsUtils.initParams(fieldModels, from, to, value);
        return jdbcTemplate.query(resultSql.toString(), params.toArray(), new SumExtractor());
    }

    public Long sumOperationSuccess(EventField fieldName, String value, Long from, Long to) {
        String sql = String.format("select %1$s, sum(amount) as sum " +
                "from fraud.events_unique " +
                "where  (eventTime >= ? and eventTime <= ? and %1$s = ? and resultStatus in (?, ?, ?))" +
                "group by %1$s", fieldName.name());
        return jdbcTemplate.query(sql, new Object[]{from, to, value, ResultStatus.ACCEPT.toString(),
                ResultStatus.NORMAL.toString(), ResultStatus.THREE_DS.toString()}, new SumExtractor());
    }

    public Long sumOperationSuccessWithGroupBy(EventField fieldName, String value, Long from, Long to,
                                               List<FieldResolver.FieldModel> fieldModels) {
        StringBuilder sql = new StringBuilder(String.format("select %1$s, sum(amount) as sum " +
                "from fraud.events_unique " +
                "where (eventTime >= ? and eventTime <= ? and %1$s = ? and resultStatus in (?, ?, ?))", fieldName.name()));
        StringBuilder sqlGroupBy = new StringBuilder(String.format("group by %1$s", fieldName.name()));
        StringBuilder resultSql = appendGroupingFields(fieldModels, sql, sqlGroupBy);
        ArrayList<Object> params = ParamsUtils.initParams(fieldModels, from, to, value, ResultStatus.ACCEPT.toString(),
                ResultStatus.NORMAL.toString(), ResultStatus.THREE_DS.toString());
        return jdbcTemplate.query(resultSql.toString(), params.toArray(), new SumExtractor());
    }

    public Long sumOperationError(EventField fieldName, String value, Long from, Long to) {
        String sql = String.format("select %1$s, sum(amount) as sum " +
                "from fraud.events_unique " +
                "where  (eventTime >= ? and eventTime <= ? and %1$s = ? and resultStatus = ?)" +
                "group by %1$s", fieldName);
        return jdbcTemplate.query(sql, new Object[]{from, to, value, ResultStatus.DECLINE.toString()},
                new SumExtractor());
    }

    public Long sumOperationErrorWithGroupBy(EventField fieldName, String value, Long from, Long to,
                                             List<FieldResolver.FieldModel> fieldModels) {
        StringBuilder sql = new StringBuilder(String.format("select %1$s, sum(amount) as sum " +
                "from fraud.events_unique " +
                "where (eventTime >= ? and eventTime <= ? and %1$s = ? and resultStatus = ?)", fieldName));
        StringBuilder sqlGroupBy = new StringBuilder(String.format("group by %1$s", fieldName));
        StringBuilder resultSql = appendGroupingFields(fieldModels, sql, sqlGroupBy);
        ArrayList<Object> params = ParamsUtils.initParams(fieldModels, from, to, value, ResultStatus.DECLINE.toString());
        return jdbcTemplate.query(resultSql.toString(), params.toArray(), new SumExtractor());
    }

    public Integer uniqCountOperation(EventField fieldNameBy, String value, EventField fieldNameCount, Long from, Long to) {
        String sql = String.format("select %1$s, uniq(%2$s) as cnt " +
                "from fraud.events_unique " +
                "where (eventTime >= ? and eventTime <= ? and %1$s = ?) " +
                "group by %1$s", fieldNameBy, fieldNameCount);
        return jdbcTemplate.query(sql, new Object[]{from, to, value}, new CountExtractor());
    }

    public Integer uniqCountOperationWithGroupBy(EventField fieldNameBy, String value, EventField fieldNameCount,
                                                 Long from, Long to, List<FieldResolver.FieldModel> fieldModels) {
        StringBuilder sql = new StringBuilder(String.format("select %1$s, uniq(%2$s) as cnt " +
                "from fraud.events_unique " +
                "where (eventTime >= ? and eventTime <= ? and %1$s = ?) ", fieldNameBy, fieldNameCount));
        StringBuilder sqlGroupBy = new StringBuilder(String.format("group by %1$s", fieldNameBy));
        StringBuilder resultSql = appendGroupingFields(fieldModels, sql, sqlGroupBy);
        ArrayList<Object> params = ParamsUtils.initParams(fieldModels, from, to, value);
        return jdbcTemplate.query(resultSql.toString(), params.toArray(), new CountExtractor());
    }
}
