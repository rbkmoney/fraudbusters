package com.rbkmoney.fraudbusters.repository;

import com.rbkmoney.fraudbusters.domain.Event;
import com.rbkmoney.fraudbusters.repository.extractor.CountExtractor;
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
            "(timestamp, ip, email, bin, fingerprint, shopId, partyId, resultStatus, amount, eventTime)" +
            " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

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

    public Integer countOperationByEmail(String email, Long from, Long to) {
        String sql = "select email, count() as cnt " +
                "from fraud.events_unique " +
                "where  (eventTime >= ? and eventTime <= ? and email = ?)" +
                "group by email ";
        return jdbcTemplate.query(sql, new Object[]{from, to, email}, new CountExtractor());
    }

    public Integer countOperationByEmailSuccess(String email, Long from, Long to) {
        String sql = "select email, count() as cnt " +
                "from fraud.events_unique " +
                "where  (eventTime >= ? and eventTime <= ? and email = ? and resultStatus in (?, ?, ?))" +
                "group by email ";
        return jdbcTemplate.query(sql, new Object[]{from, to, email, ResultStatus.ACCEPT.toString(),
                ResultStatus.NORMAL.toString(), ResultStatus.THREE_DS.toString()}, new CountExtractor());
    }

    public Integer countOperationByEmailError(String email, Long from, Long to) {
        String sql = "select email, count() as cnt " +
                "from fraud.events_unique " +
                "where  (eventTime >= ? and eventTime <= ? and email = ? and resultStatus = ?)" +
                "group by email ";
        return jdbcTemplate.query(sql, new Object[]{from, to, email, ResultStatus.DECLINE.toString()},
                new CountExtractor());
    }
}
