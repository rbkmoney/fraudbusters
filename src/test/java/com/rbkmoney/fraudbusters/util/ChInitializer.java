package com.rbkmoney.fraudbusters.util;

import org.testcontainers.containers.ClickHouseContainer;
import ru.yandex.clickhouse.ClickHouseDataSource;
import ru.yandex.clickhouse.settings.ClickHouseProperties;

import java.sql.Connection;
import java.sql.SQLException;

public class ChInitializer {

    public static void initAllScripts(ClickHouseContainer clickHouseContainer) throws SQLException {
        try (Connection connection = getSystemConn(clickHouseContainer)) {
            execAllInFile(connection, "sql/db_init.sql");
            execAllInFile(connection, "sql/V2__create_events_p2p.sql");
            execAllInFile(connection, "sql/V3__create_fraud_payments.sql");
            execAllInFile(connection, "sql/TEST_analytics_data.sql");
            execAllInFile(connection, "sql/V3__create_payment.sql");
        }
    }

    public static void execAllInFile(Connection connection, String s) throws SQLException {
        String sql = FileUtil.getFile(s);
        String[] split = sql.split(";");
        for (String exec : split) {
            connection.createStatement().execute(exec);
        }
    }

    public static Connection getSystemConn(ClickHouseContainer clickHouseContainer) throws SQLException {
        ClickHouseProperties properties = new ClickHouseProperties();
        ClickHouseDataSource dataSource = new ClickHouseDataSource(clickHouseContainer.getJdbcUrl(), properties);
        return dataSource.getConnection();
    }

}
