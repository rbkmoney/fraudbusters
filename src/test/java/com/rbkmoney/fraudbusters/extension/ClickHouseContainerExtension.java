package com.rbkmoney.fraudbusters.extension;

import com.rbkmoney.clickhouse.initializer.ChInitializer;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.testcontainers.containers.ClickHouseContainer;

import java.sql.SQLException;
import java.util.List;

public class ClickHouseContainerExtension implements BeforeAllCallback {

    private static final String VERSION = "yandex/clickhouse-server:19.17";

    public static ClickHouseContainer CLICKHOUSE_CONTAINER;

    @Override
    public void beforeAll(ExtensionContext context) throws Exception {
        CLICKHOUSE_CONTAINER = new ClickHouseContainer(VERSION);
        CLICKHOUSE_CONTAINER.start();
        ChInitializer.initAllScripts(CLICKHOUSE_CONTAINER, List.of(
                "sql/db_init.sql",
                "sql/V3__create_fraud_payments.sql",
                "sql/V4__create_payment.sql",
                "sql/V5__add_fields.sql",
                "sql/V6__add_result_fields_payment.sql",
                "sql/V7__add_fields.sql",
                "sql/V8__create_withdrawal.sql"
        ));
    }

    public static void executeScripts(List<String> scriptFilePaths) {
        try {
            ChInitializer.initAllScripts(CLICKHOUSE_CONTAINER, scriptFilePaths);
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }
}
