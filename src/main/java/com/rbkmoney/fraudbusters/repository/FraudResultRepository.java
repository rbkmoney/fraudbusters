package com.rbkmoney.fraudbusters.repository;

import com.rbkmoney.fraudbusters.domain.FraudResult;
import com.rbkmoney.fraudbusters.repository.setter.FraudResultBatchPreparedStatementSetter;
import com.rbkmoney.fraudo.model.FraudModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Calendar;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class FraudResultRepository implements CrudRepository<FraudResult> {

    private final JdbcTemplate jdbcTemplate;

    private static final String INSERT = "INSERT INTO fraud.events_unique " +
            "(timestamp, ip, email, bin, fingerprint, shopId, partyId,resultStatus, eventTime)" +
            " Values (?,?,?,?,?,?,?,?,?)";

    @Override
    public void insert(FraudResult value) {
        if (value != null && value.getFraudModel() != null) {
            FraudModel fraudModel = value.getFraudModel();
            jdbcTemplate.update(INSERT, new java.sql.Date(Calendar.getInstance().getTime().getTime()), fraudModel.getIp(),
                    fraudModel.getEmail(), fraudModel.getBin(), fraudModel.getFingerprint(), fraudModel.getShopId(),
                    fraudModel.getPartyId(), value.getResultStatus().name(), Instant.now().toEpochMilli());
        }
    }

    @Override
    public void insertBatch(List<FraudResult> batch) {
        if (batch != null && !batch.isEmpty()) {
            jdbcTemplate.batchUpdate(INSERT, new FraudResultBatchPreparedStatementSetter(batch));
        }
    }

    public Integer countOperationByEmail(String email, Long from, Long to) {
        return jdbcTemplate.queryForObject("select email, count() as cnt from fraud.events_unique " +
                        "where  (eventTime >= ? and eventTime <= ? and email = ?)" +
                        "group by email ", new Object[]{from, to, email},
                (resultSet, i) -> resultSet.getInt("cnt"));
    }
}
