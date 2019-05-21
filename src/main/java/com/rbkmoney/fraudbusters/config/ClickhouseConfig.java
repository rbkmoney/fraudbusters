package com.rbkmoney.fraudbusters.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.clickhouse.ClickHouseDataSource;
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

    @Bean
    public ClickHouseDataSource clickHouseDataSource() {
        Properties info = new Properties();
        info.setProperty(ClickHouseQueryParam.USER.getKey(), user);
        info.setProperty(ClickHouseQueryParam.PASSWORD.getKey(), password);
        return new ClickHouseDataSource(dbUrl, info);
    }

    @Bean
    @Autowired
    public JdbcTemplate jdbcTemplate(DataSource clickHouseDataSource) {
        return new JdbcTemplate(clickHouseDataSource);
    }

}
