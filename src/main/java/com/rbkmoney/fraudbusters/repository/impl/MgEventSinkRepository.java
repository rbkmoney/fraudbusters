package com.rbkmoney.fraudbusters.repository.impl;

import com.rbkmoney.fraudbusters.constant.ResultStatus;
import com.rbkmoney.fraudbusters.fraud.model.FieldModel;
import com.rbkmoney.fraudbusters.repository.extractor.CountExtractor;
import com.rbkmoney.fraudbusters.repository.extractor.SumExtractor;
import com.rbkmoney.fraudbusters.repository.util.ParamsInitiator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MgEventSinkRepository {

    private final JdbcTemplate jdbcTemplate;

    public Integer countOperationByField(String fieldName, String value, Long from, Long to) {
        String sql = String.format("select %1$s, count() as cnt " +
                "from fraud.events_sink_mg " +
                "where (eventTime >= ? and eventTime <= ? and %1$s = ?)" +
                "group by %1$s", fieldName);
        return jdbcTemplate.query(sql, new Object[]{from, to, value}, new CountExtractor());
    }

    public Integer countOperationByFieldWithGroupBy(String fieldName, String value, Long from, Long to,
                                                    List<FieldModel> fieldModels) {
        StringBuilder sql = new StringBuilder(String.format("select %1$s, count() as cnt " +
                "from fraud.events_sink_mg " +
                "where eventTime >= ? and eventTime <= ? and %1$s = ? ", fieldName));
        StringBuilder sqlGroupBy = new StringBuilder(String.format(" group by %1$s ", fieldName));
        StringBuilder resultSql = appendGroupingFields(fieldModels, sql, sqlGroupBy);
        ArrayList<Object> objects = ParamsInitiator.initParams(fieldModels, from, to, value);
        return jdbcTemplate.query(resultSql.toString(), objects.toArray(), new CountExtractor());
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

    public Integer countOperationSuccessWithGroupBy(String fieldName, String value, Long from, Long to,
                                                    List<FieldModel> fieldModels) {
        StringBuilder sql = new StringBuilder(String.format("select %1$s, count() as cnt " +
                "from fraud.events_sink_mg " +
                "where eventTime >= ? and eventTime <= ? and %1$s = ? and resultStatus = ? ", fieldName));
        StringBuilder sqlGroupBy = new StringBuilder(String.format("group by %1$s", fieldName));
        StringBuilder resultSql = appendGroupingFields(fieldModels, sql, sqlGroupBy);
        ArrayList<Object> params = ParamsInitiator.initParams(fieldModels, from, to, value, ResultStatus.CAPTURED.name());
        return jdbcTemplate.query(resultSql.toString(), params.toArray(), new CountExtractor());
    }

    public Integer countOperationErrorWithGroupBy(String fieldName, String value, Long from, Long to,
                                                  List<FieldModel> fieldModels) {
        StringBuilder sql = new StringBuilder(String.format("select %1$s, count() as cnt " +
                "from fraud.events_sink_mg " +
                "where eventTime >= ? and eventTime <= ? and %1$s = ? and resultStatus = ? ", fieldName));
        StringBuilder sqlGroupBy = new StringBuilder(String.format("group by %1$s", fieldName));
        StringBuilder resultSql = appendGroupingFields(fieldModels, sql, sqlGroupBy);
        ArrayList<Object> params = ParamsInitiator.initParams(fieldModels, from, to, value, ResultStatus.FAILED.name());
        return jdbcTemplate.query(resultSql.toString(), params.toArray(), new CountExtractor());
    }

    public Long sumOperationSuccessWithGroupBy(String fieldName, String value, Long from, Long to,
                                               List<FieldModel> fieldModels) {
        StringBuilder sql = new StringBuilder(String.format("select %1$s, sum(amount) as sum " +
                "from fraud.events_sink_mg " +
                "where (eventTime >= ? and eventTime <= ? and %1$s = ? and resultStatus = ?)", fieldName));
        StringBuilder sqlGroupBy = new StringBuilder(String.format("group by %1$s", fieldName));
        StringBuilder resultSql = appendGroupingFields(fieldModels, sql, sqlGroupBy);
        ArrayList<Object> params = ParamsInitiator.initParams(fieldModels, from, to, value, ResultStatus.CAPTURED.name());
        return jdbcTemplate.query(resultSql.toString(), params.toArray(), new SumExtractor());
    }

    public Long sumOperationErrorWithGroupBy(String fieldName, String value, Long from, Long to,
                                             List<FieldModel> fieldModels) {
        StringBuilder sql = new StringBuilder(String.format("select %1$s, sum(amount) as sum " +
                "from fraud.events_sink_mg " +
                "where (eventTime >= ? and eventTime <= ? and %1$s = ? and resultStatus = ?)", fieldName));
        StringBuilder sqlGroupBy = new StringBuilder(String.format("group by %1$s", fieldName));
        StringBuilder resultSql = appendGroupingFields(fieldModels, sql, sqlGroupBy);
        ArrayList<Object> params = ParamsInitiator.initParams(fieldModels, from, to, value, ResultStatus.FAILED.name());
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
                                                 Long from, Long to, List<FieldModel> fieldModels) {
        StringBuilder sql = new StringBuilder(String.format("select %1$s, uniq(%2$s) as cnt " +
                "from fraud.events_sink_mg " +
                "where (eventTime >= ? and eventTime <= ? and %1$s = ?) ", fieldNameBy, fieldNameCount));
        StringBuilder sqlGroupBy = new StringBuilder(String.format("group by %1$s", fieldNameBy));
        StringBuilder resultSql = appendGroupingFields(fieldModels, sql, sqlGroupBy);
        ArrayList<Object> params = ParamsInitiator.initParams(fieldModels, from, to, value);
        return jdbcTemplate.query(resultSql.toString(), params.toArray(), new CountExtractor());
    }
}
