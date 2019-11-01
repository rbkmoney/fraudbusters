package com.rbkmoney.fraudbusters.repository;

import com.google.common.collect.Lists;
import com.rbkmoney.fraudbusters.constant.ClickhouseSchemeNames;
import com.rbkmoney.fraudbusters.domain.EventP2P;
import com.rbkmoney.fraudbusters.fraud.resolver.DBPaymentFieldResolver;
import com.rbkmoney.fraudbusters.repository.extractor.CountExtractor;
import com.rbkmoney.fraudbusters.repository.extractor.SumExtractor;
import com.rbkmoney.fraudbusters.repository.setter.EventP2PBatchPreparedStatementSetter;
import com.rbkmoney.fraudbusters.repository.setter.EventP2PParametersGenerator;
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
public class EventP2PRepository implements CrudRepository<EventP2P> {

    private final JdbcTemplate jdbcTemplate;

    private static final String INSERT = "INSERT INTO fraud.events_p_to_p " +
            "(timestamp, ip, email, bin, fingerprint, shopId, partyId, resultStatus, amount, eventTime, " +
            "country, checkedRule, bankCountry, currency, invoiceId, maskedPan, bankName, cardToken, paymentId, checkedTemplate)" +
            " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    @Override
    public void insert(EventP2P value) {
        if (value != null) {
            Map<String, Object> parameters = EventP2PParametersGenerator.generateParamsByFraudModel(value);
            SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate.getDataSource())
                    .withSchemaName(ClickhouseSchemeNames.FRAUD)
                    .withTableName(ClickhouseSchemeNames.EVENTS_UNIQUE);
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

    public Integer countOperationByField(String fieldName, String value, Long from, Long to) {
        String sql = String.format("select %1$s, count() as cnt " +
                "from fraud.events_p_to_p " +
                "where (eventTime >= ? and eventTime <= ? and %1$s = ?)" +
                "group by %1$s", fieldName);
        return jdbcTemplate.query(sql, new Object[]{from, to, value}, new CountExtractor());
    }

    public Integer countOperationByFieldWithGroupBy(String fieldName, String value, Long from, Long to,
                                                    List<DBPaymentFieldResolver.FieldModel> fieldModels) {
        StringBuilder sql = new StringBuilder(String.format("select %1$s, count() as cnt " +
                "from fraud.events_p_to_p " +
                "where eventTime >= ? and eventTime <= ? and %1$s = ? ", fieldName));
        StringBuilder sqlGroupBy = new StringBuilder(String.format(" group by %1$s ", fieldName));
        StringBuilder resultSql = appendGroupingFields(fieldModels, sql, sqlGroupBy);
        ArrayList<Object> objects = ParamsUtils.initParams(fieldModels, from, to, value);
        return jdbcTemplate.query(resultSql.toString(), objects.toArray(), new CountExtractor());
    }

    private StringBuilder appendGroupingFields(List<DBPaymentFieldResolver.FieldModel> fieldModels, StringBuilder sql, StringBuilder sqlGroupBy) {
        if (fieldModels != null) {
            for (DBPaymentFieldResolver.FieldModel fieldModel : fieldModels) {
                sql.append(" and ").append(fieldModel.getName()).append("=? ");
                sqlGroupBy.append(", ").append(fieldModel.getName());
            }
        }
        return sql.append(sqlGroupBy.toString());
    }

    public Long sumOperationByField(String fieldName, String value, Long from, Long to) {
        String sql = String.format("select %1$s, sum(amount) as sum " +
                "from fraud.events_p_to_p " +
                "where (eventTime >= ? and eventTime <= ? and %1$s = ?)" +
                "group by %1$s", fieldName);
        return jdbcTemplate.query(sql, new Object[]{from, to, value}, new SumExtractor());
    }

    public Long sumOperationByFieldWithGroupBy(String fieldName, String value, Long from, Long to,
                                               List<DBPaymentFieldResolver.FieldModel> fieldModels) {
        StringBuilder sql = new StringBuilder(String.format("select %1$s, sum(amount) as sum " +
                "from fraud.events_unique " +
                "where eventTime >= ? and eventTime <= ? and %1$s = ? ", fieldName));
        StringBuilder sqlGroupBy = new StringBuilder(String.format("group by %1$s", fieldName));
        StringBuilder resultSql = appendGroupingFields(fieldModels, sql, sqlGroupBy);
        ArrayList<Object> params = ParamsUtils.initParams(fieldModels, from, to, value);
        return jdbcTemplate.query(resultSql.toString(), params.toArray(), new SumExtractor());
    }

    public Integer uniqCountOperation(String fieldNameBy, String value, String fieldNameCount, Long from, Long to) {
        String sql = String.format("select %1$s, uniq(%2$s) as cnt " +
                "from fraud.events_p_to_p " +
                "where (eventTime >= ? and eventTime <= ? and %1$s = ?) " +
                "group by %1$s", fieldNameBy, fieldNameCount);
        return jdbcTemplate.query(sql, new Object[]{from, to, value}, new CountExtractor());
    }

    public Integer uniqCountOperationWithGroupBy(String fieldNameBy, String value, String fieldNameCount,
                                                 Long from, Long to, List<DBPaymentFieldResolver.FieldModel> fieldModels) {
        StringBuilder sql = new StringBuilder(String.format("select %1$s, uniq(%2$s) as cnt " +
                "from fraud.events_p_to_p " +
                "where (eventTime >= ? and eventTime <= ? and %1$s = ?) ", fieldNameBy, fieldNameCount));
        StringBuilder sqlGroupBy = new StringBuilder(String.format("group by %1$s", fieldNameBy));
        StringBuilder resultSql = appendGroupingFields(fieldModels, sql, sqlGroupBy);
        ArrayList<Object> params = ParamsUtils.initParams(fieldModels, from, to, value);
        return jdbcTemplate.query(resultSql.toString(), params.toArray(), new CountExtractor());
    }
}
