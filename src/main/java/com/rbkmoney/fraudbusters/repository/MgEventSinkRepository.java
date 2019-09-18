package com.rbkmoney.fraudbusters.repository;

import com.google.common.collect.Lists;
import com.rbkmoney.fraudbusters.constant.ClickhouseSchemeNames;
import com.rbkmoney.fraudbusters.domain.MgEventSinkRow;
import com.rbkmoney.fraudbusters.fraud.resolver.FieldResolver;
import com.rbkmoney.fraudbusters.repository.extractor.CountExtractor;
import com.rbkmoney.fraudbusters.repository.extractor.SumExtractor;
import com.rbkmoney.fraudbusters.repository.setter.MgEventSinkBatchPreparedStatementSetter;
import com.rbkmoney.fraudbusters.repository.setter.MgEventSinkParametersGenerator;
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
public class MgEventSinkRepository implements CrudRepository<MgEventSinkRow> {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void insert(MgEventSinkRow value) {
        if (value != null) {
            Map<String, Object> parameters = MgEventSinkParametersGenerator.generateParamsByFraudModel(value);
            SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate.getDataSource())
                    .withSchemaName(ClickhouseSchemeNames.FRAUD)
                    .withTableName(ClickhouseSchemeNames.EVENTS_SINK_MG);
            simpleJdbcInsert.setColumnNames(Lists.newArrayList(parameters.keySet()));
            simpleJdbcInsert
                    .execute(parameters);
        }
    }

    @Override
    public void insertBatch(List<MgEventSinkRow> mgEventSinkRows) {
        if (mgEventSinkRows != null && !mgEventSinkRows.isEmpty()) {
            jdbcTemplate.batchUpdate(MgEventSinkBatchPreparedStatementSetter.INSERT, new MgEventSinkBatchPreparedStatementSetter(mgEventSinkRows));
        }
    }

    public Integer countOperationByField(String fieldName, String value, Long from, Long to) {
        String sql = String.format("select %1$s, count() as cnt " +
                "from fraud.events_sink_mg " +
                "where (eventTime >= ? and eventTime <= ? and %1$s = ?)" +
                "group by %1$s", fieldName);
        return jdbcTemplate.query(sql, new Object[]{from, to, value}, new CountExtractor());
    }

    public Integer countOperationByFieldWithGroupBy(String fieldName, String value, Long from, Long to,
                                                    List<FieldResolver.FieldModel> fieldModels) {
        StringBuilder sql = new StringBuilder(String.format("select %1$s, count() as cnt " +
                "from fraud.events_sink_mg " +
                "where eventTime >= ? and eventTime <= ? and %1$s = ? ", fieldName));
        StringBuilder sqlGroupBy = new StringBuilder(String.format(" group by %1$s ", fieldName));
        StringBuilder resultSql = appendGroupingFields(fieldModels, sql, sqlGroupBy);
        ArrayList<Object> objects = ParamsUtils.initParams(fieldModels, from, to, value);
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

    public Integer countOperationSuccess(String fieldName, String value, Long from, Long to) {
        String sql = String.format("select %1$s, count() as cnt " +
                "from fraud.events_sink_mg " +
                "where (eventTime >= ? and eventTime <= ? and %1$s = ? and resultStatus in (?, ?, ?))" +
                "group by %1$s", fieldName);
        return jdbcTemplate.query(sql, new Object[]{from, to, value, ResultStatus.ACCEPT.toString(),
                ResultStatus.NORMAL.toString(), ResultStatus.THREE_DS.toString()}, new CountExtractor());
    }

    public Integer countOperationSuccessWithGroupBy(String fieldName, String value, Long from, Long to,
                                                    List<FieldResolver.FieldModel> fieldModels) {
        StringBuilder sql = new StringBuilder(String.format("select %1$s, count() as cnt " +
                "from fraud.events_sink_mg " +
                "where eventTime >= ? and eventTime <= ? and %1$s = ? and resultStatus in (?, ?, ?) ", fieldName));
        StringBuilder sqlGroupBy = new StringBuilder(String.format("group by %1$s", fieldName));
        StringBuilder resultSql = appendGroupingFields(fieldModels, sql, sqlGroupBy);
        ArrayList<Object> params = ParamsUtils.initParams(fieldModels, from, to, value, ResultStatus.ACCEPT.toString(),
                ResultStatus.NORMAL.toString(), ResultStatus.THREE_DS.toString());
        return jdbcTemplate.query(resultSql.toString(), params.toArray(), new CountExtractor());
    }

    public Integer countOperationError(String fieldName, String value, Long from, Long to) {
        String sql = String.format("select %1$s, count() as cnt " +
                "from fraud.events_sink_mg " +
                "where (eventTime >= ? and eventTime <= ? and %1$s = ? and resultStatus = ?)" +
                "group by %1$s", fieldName);
        return jdbcTemplate.query(sql, new Object[]{from, to, value, ResultStatus.DECLINE.toString()}, new CountExtractor());
    }

    public Integer countOperationErrorWithGroupBy(String fieldName, String value, Long from, Long to,
                                                  List<FieldResolver.FieldModel> fieldModels) {
        StringBuilder sql = new StringBuilder(String.format("select %1$s, count() as cnt " +
                "from fraud.events_sink_mg " +
                "where eventTime >= ? and eventTime <= ? and %1$s = ? and resultStatus = ? ", fieldName));
        StringBuilder sqlGroupBy = new StringBuilder(String.format("group by %1$s", fieldName));
        StringBuilder resultSql = appendGroupingFields(fieldModels, sql, sqlGroupBy);
        ArrayList<Object> params = ParamsUtils.initParams(fieldModels, from, to, value, ResultStatus.DECLINE.toString());
        return jdbcTemplate.query(resultSql.toString(), params.toArray(), new CountExtractor());
    }

    public Long sumOperationByField(String fieldName, String value, Long from, Long to) {
        String sql = String.format("select %1$s, sum(amount) as sum " +
                "from fraud.events_sink_mg " +
                "where (eventTime >= ? and eventTime <= ? and %1$s = ?)" +
                "group by %1$s", fieldName);
        return jdbcTemplate.query(sql, new Object[]{from, to, value}, new SumExtractor());
    }

    public Long sumOperationByFieldWithGroupBy(String fieldName, String value, Long from, Long to,
                                               List<FieldResolver.FieldModel> fieldModels) {
        StringBuilder sql = new StringBuilder(String.format("select %1$s, sum(amount) as sum " +
                "from fraud.events_sink_mg " +
                "where eventTime >= ? and eventTime <= ? and %1$s = ? ", fieldName));
        StringBuilder sqlGroupBy = new StringBuilder(String.format("group by %1$s", fieldName));
        StringBuilder resultSql = appendGroupingFields(fieldModels, sql, sqlGroupBy);
        ArrayList<Object> params = ParamsUtils.initParams(fieldModels, from, to, value);
        return jdbcTemplate.query(resultSql.toString(), params.toArray(), new SumExtractor());
    }

    public Long sumOperationSuccess(String fieldName, String value, Long from, Long to) {
        String sql = String.format("select %1$s, sum(amount) as sum " +
                "from fraud.events_sink_mg " +
                "where  (eventTime >= ? and eventTime <= ? and %1$s = ? and resultStatus in (?, ?, ?))" +
                "group by %1$s", fieldName);
        return jdbcTemplate.query(sql, new Object[]{from, to, value, ResultStatus.ACCEPT.toString(),
                ResultStatus.NORMAL.toString(), ResultStatus.THREE_DS.toString()}, new SumExtractor());
    }

    public Long sumOperationSuccessWithGroupBy(String fieldName, String value, Long from, Long to,
                                               List<FieldResolver.FieldModel> fieldModels) {
        StringBuilder sql = new StringBuilder(String.format("select %1$s, sum(amount) as sum " +
                "from fraud.events_sink_mg " +
                "where (eventTime >= ? and eventTime <= ? and %1$s = ? and resultStatus in (?, ?, ?))", fieldName));
        StringBuilder sqlGroupBy = new StringBuilder(String.format("group by %1$s", fieldName));
        StringBuilder resultSql = appendGroupingFields(fieldModels, sql, sqlGroupBy);
        ArrayList<Object> params = ParamsUtils.initParams(fieldModels, from, to, value, ResultStatus.ACCEPT.toString(),
                ResultStatus.NORMAL.toString(), ResultStatus.THREE_DS.toString());
        return jdbcTemplate.query(resultSql.toString(), params.toArray(), new SumExtractor());
    }

    public Long sumOperationError(String fieldName, String value, Long from, Long to) {
        String sql = String.format("select %1$s, sum(amount) as sum " +
                "from fraud.events_sink_mg " +
                "where  (eventTime >= ? and eventTime <= ? and %1$s = ? and resultStatus = ?)" +
                "group by %1$s", fieldName);
        return jdbcTemplate.query(sql, new Object[]{from, to, value, ResultStatus.DECLINE.toString()},
                new SumExtractor());
    }

    public Long sumOperationErrorWithGroupBy(String fieldName, String value, Long from, Long to,
                                             List<FieldResolver.FieldModel> fieldModels) {
        StringBuilder sql = new StringBuilder(String.format("select %1$s, sum(amount) as sum " +
                "from fraud.events_sink_mg " +
                "where (eventTime >= ? and eventTime <= ? and %1$s = ? and resultStatus = ?)", fieldName));
        StringBuilder sqlGroupBy = new StringBuilder(String.format("group by %1$s", fieldName));
        StringBuilder resultSql = appendGroupingFields(fieldModels, sql, sqlGroupBy);
        ArrayList<Object> params = ParamsUtils.initParams(fieldModels, from, to, value, ResultStatus.DECLINE.toString());
        return jdbcTemplate.query(resultSql.toString(), params.toArray(), new SumExtractor());
    }

    public Integer uniqCountOperation(String fieldNameBy, String value, String fieldNameCount, Long from, Long to) {
        String sql = String.format("select %1$s, uniq(%2$s) as cnt " +
                "from fraud.events_sink_mg " +
                "where (eventTime >= ? and eventTime <= ? and %1$s = ?) " +
                "group by %1$s", fieldNameBy, fieldNameCount);
        return jdbcTemplate.query(sql, new Object[]{from, to, value}, new CountExtractor());
    }

    public Integer uniqCountOperationWithGroupBy(String fieldNameBy, String value, String fieldNameCount,
                                                 Long from, Long to, List<FieldResolver.FieldModel> fieldModels) {
        StringBuilder sql = new StringBuilder(String.format("select %1$s, uniq(%2$s) as cnt " +
                "from fraud.events_sink_mg " +
                "where (eventTime >= ? and eventTime <= ? and %1$s = ?) ", fieldNameBy, fieldNameCount));
        StringBuilder sqlGroupBy = new StringBuilder(String.format("group by %1$s", fieldNameBy));
        StringBuilder resultSql = appendGroupingFields(fieldModels, sql, sqlGroupBy);
        ArrayList<Object> params = ParamsUtils.initParams(fieldModels, from, to, value);
        return jdbcTemplate.query(resultSql.toString(), params.toArray(), new CountExtractor());
    }
}
