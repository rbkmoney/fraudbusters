package com.rbkmoney.fraudbusters.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import ru.yandex.clickhouse.ClickHouseDataSource;
import ru.yandex.clickhouse.settings.ClickHouseConnectionSettings;
import ru.yandex.clickhouse.settings.ClickHouseQueryParam;

import javax.sql.DataSource;

import java.util.Properties;

@Configuration
public class ClickhouseConfig {

    @Value("${clickhouse.db.url}")
    private String dbUrl;

    @Value("${clickhouse.db.user}")
    private String user;

    @Value("${clickhouse.db.password}")
    private String password;

    @Value("${clickhouse.db.compress}")
    private String compress;

    @Value("${clickhouse.db.connection.timeout:60000}")
    private String connectionTimeout;

    @Value("${clickhouse.db.socket.timeout:60000}")
    private String socketTimeout;

    @Bean
    public ClickHouseDataSource clickHouseDataSource() {
        Properties info = new Properties();
        info.put(ClickHouseQueryParam.USER.getKey(), user);
        info.put(ClickHouseQueryParam.PASSWORD.getKey(), password);
        info.put(ClickHouseQueryParam.COMPRESS.getKey(), compress);
        return new ClickHouseDataSource(dbUrl, info);
    }

    @Bean
    @Autowired
    public JdbcTemplate jdbcTemplate(DataSource clickHouseDataSource) {
        return new JdbcTemplate(clickHouseDataSource);
    }

    @Bean
    public JdbcTemplate longQueryJdbcTemplate() {
        Properties info = new Properties();
        info.put(ClickHouseQueryParam.USER.getKey(), user);
        info.put(ClickHouseQueryParam.PASSWORD.getKey(), password);
        info.put(ClickHouseQueryParam.COMPRESS.getKey(), true);
        info.put(ClickHouseQueryParam.CONNECT_TIMEOUT.getKey(), connectionTimeout);
        info.put(ClickHouseConnectionSettings.CONNECTION_TIMEOUT.getKey(), Integer.parseInt(connectionTimeout));
        info.put(ClickHouseConnectionSettings.SOCKET_TIMEOUT.getKey(), Integer.parseInt(socketTimeout));
        return new JdbcTemplate(new ClickHouseDataSource(dbUrl, info));
    }

    @Bean
    public NamedParameterJdbcTemplate namedParameterJdbcTemplate() {
        return new NamedParameterJdbcTemplate(clickHouseDataSource());
    }

}
