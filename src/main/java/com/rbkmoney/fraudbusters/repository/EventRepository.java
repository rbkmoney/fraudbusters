package com.rbkmoney.fraudbusters.repository;

import com.rbkmoney.fraudbusters.constant.EventField;
import com.rbkmoney.fraudbusters.domain.Event;
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

import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventRepository implements CrudRepository<Event> {

    private final JdbcTemplate jdbcTemplate;

    private static final String INSERT = "INSERT INTO fraud.events_unique " +
            "(timestamp, ip, email, bin, fingerprint, shopId, partyId, resultStatus, amount, eventTime, country, checkedRule, bankCountry)" +
            " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    @Override
    public void insert(Event value) {
        if (value != null) {
            Map<String, Object> parameters = EventParametersGenerator.generateParamsByFraudModel(value);
            new SimpleJdbcInsert(jdbcTemplate.getDataSource())
                    .withSchemaName("fraud")
                    .withTableName("events_unique")
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

    public Integer countOperationSuccess(EventField fieldName, String value, Long from, Long to) {
        String sql = String.format("select %1$s, count() as cnt " +
                "from fraud.events_unique " +
                "where (eventTime >= ? and eventTime <= ? and %1$s = ? and resultStatus in (?, ?, ?))" +
                "group by %1$s", fieldName.name());
        return jdbcTemplate.query(sql, new Object[]{from, to, value, ResultStatus.ACCEPT.toString(),
                ResultStatus.NORMAL.toString(), ResultStatus.THREE_DS.toString()}, new CountExtractor());
    }

    public Integer countOperationError(EventField fieldName, String value, Long from, Long to) {
        String sql = String.format("select %1$s, count() as cnt " +
                "from fraud.events_unique " +
                "where (eventTime >= ? and eventTime <= ? and %1$s = ? and resultStatus = ?)" +
                "group by %1$s", fieldName.name());
        return jdbcTemplate.query(sql, new Object[]{from, to, value, ResultStatus.DECLINE.toString()},
                new CountExtractor());
    }

    public Long sumOperationByField(EventField fieldName, String value, Long from, Long to) {
        String sql = String.format("select %1$s, sum(amount) as sum " +
                "from fraud.events_unique " +
                "where (eventTime >= ? and eventTime <= ? and %1$s = ?)" +
                "group by %1$s", fieldName.name());
        return jdbcTemplate.query(sql, new Object[]{from, to, value}, new SumExtractor());
    }

    public Long sumOperationSuccess(EventField fieldName, String value, Long from, Long to) {
        String sql = String.format("select %1$s, sum(amount) as sum " +
                "from fraud.events_unique " +
                "where (eventTime >= ? and eventTime <= ? and %1$s = ? and resultStatus in (?, ?, ?))" +
                "group by %1$s", fieldName.name());
        return jdbcTemplate.query(sql, new Object[]{from, to, value, ResultStatus.ACCEPT.toString(),
                ResultStatus.NORMAL.toString(), ResultStatus.THREE_DS.toString()}, new SumExtractor());
    }

    public Long sumOperationError(EventField fieldName, String value, Long from, Long to) {
        String sql = String.format("select %1$s, sum(amount) as sum " +
                "from fraud.events_unique " +
                "where (eventTime >= ? and eventTime <= ? and %1$s = ? and resultStatus = ?)" +
                "group by %1$s", fieldName);
        return jdbcTemplate.query(sql, new Object[]{from, to, value, ResultStatus.DECLINE.toString()},
                new SumExtractor());
    }

    public Integer uniqCountOperation(EventField fieldNameBy, String value, EventField fieldNameCount, Long from, Long to) {
        String sql = String.format("select %1$s, uniq(%2$s) as cnt " +
                "from fraud.events_unique " +
                "where (eventTime >= ? and eventTime <= ? and %1$s = ?) " +
                "group by %1$s", fieldNameBy, fieldNameCount);
        return jdbcTemplate.query(sql, new Object[]{from, to, value}, new CountExtractor());
    }
}
