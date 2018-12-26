package com.rbkmoney.fraudbusters.repository;

import com.rbkmoney.fraudo.model.FraudModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Calendar;

@Slf4j
@Service
public class CountRepository implements CrudRepository<FraudModel> {

    private static final String DB_URL = "jdbc:clickhouse://localhost:8123/default";

    public Connection connection() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }

    @Override
    public void create(FraudModel value) {
        if (value != null) {
            String query = "INSERT INTO events_unique (timestamp, ip, email, bin, fingerprint, shopId, partyId)" +
                    " Values (?,?,?,?,?,?,?)";
            try (Connection connection = connection(); PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setDate(1, new java.sql.Date(Calendar.getInstance().getTime().getTime()));
                statement.setString(2, value.getIp());
                statement.setString(3, value.getEmail());
                statement.setString(4, value.getBin());
                statement.setString(5, value.getFingerprint());
                statement.setString(6, value.getShopId());
                statement.setString(7, value.getPartyId());
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

}
