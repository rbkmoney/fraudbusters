package com.rbkmoney.fraudbusters.repository;

import com.google.common.base.Strings;
import com.rbkmoney.fraudbusters.domain.FraudResult;
import com.rbkmoney.fraudo.model.FraudModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.clickhouse.ClickHouseDataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.util.Calendar;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class FraudResultRepository implements CrudRepository<FraudResult> {

    private final ClickHouseDataSource clickHouseDataSource;

    public static final String INSERT = "INSERT INTO fraud.events_unique " +
            "(timestamp, ip, email, bin, fingerprint, shopId, partyId,resultStatus, eventTime)" +
            " Values (?,?,?,?,?,?,?,?, ?)";

    @Override
    public void insert(FraudResult value) {
        if (value != null && value.getFraudModel() != null) {
            try (Connection connection = clickHouseDataSource.getConnection(); PreparedStatement statement =
                    connection.prepareStatement(INSERT)) {
                appendStatement(value, statement);
                statement.executeUpdate();
            } catch (SQLException e) {
                log.error("Error when CountRepository insert e: ", e);
            }
        }
    }

    private void appendStatement(FraudResult value, PreparedStatement statement) throws SQLException {
        FraudModel fraudModel = value.getFraudModel();
        statement.setDate(1, new java.sql.Date(Calendar.getInstance().getTime().getTime()));
        statement.setString(2, fraudModel.getIp());
        statement.setString(3, fraudModel.getEmail());
        statement.setString(4, fraudModel.getBin());
        statement.setString(5, fraudModel.getFingerprint());
        statement.setString(6, fraudModel.getShopId());
        statement.setString(7, fraudModel.getPartyId());
        statement.setString(8, value.getResultStatus().name());
        statement.setLong(9, Instant.now().toEpochMilli());

    }

    @Override
    public void insertBatch(List<FraudResult> batch) {
        if (batch != null && !batch.isEmpty()) {
            try (Connection connection = clickHouseDataSource.getConnection();
                 PreparedStatement statement = connection.prepareStatement(INSERT)) {
                for (FraudResult fraudResult : batch) {
                    addBatch(statement, fraudResult);
                }
                statement.executeBatch();
            } catch (SQLException e) {
                log.error("Error when CountRepository insert e: ", e);
            }
        }
    }

    private void addBatch(PreparedStatement statement, FraudResult fraudResult) {
        try {
            appendStatement(fraudResult, statement);
            statement.addBatch();
        } catch (SQLException e) {
            log.error("When add batch e: ", e);
        }
    }

    public Long countOperationByEmail(String email, Long from, Long to) {
        long result = 0;
        if (!Strings.isNullOrEmpty(email)) {
            String query = "select email, count() as cnt from fraud.events_unique " +
                    "where  (eventTime >= ? and eventTime <= ? and email = ?)" +
                    "group by email ";
            ResultSet rs = null;
            try (Connection connection = clickHouseDataSource.getConnection();
                 PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setLong(1, from);
                statement.setLong(2, to);
                statement.setString(3, email);
                rs = statement.executeQuery();
                rs.next();
                result = rs.getInt("cnt");
            } catch (SQLException e) {
                log.error("Error when CountRepository insert e: ", e);
            } finally {
                try {
                    if (rs != null) {
                        rs.close();
                    }
                } catch (SQLException e) {
                    log.error("Error when CountRepository rs.close() ", e);
                }
            }
        }
        return result;
    }
}
