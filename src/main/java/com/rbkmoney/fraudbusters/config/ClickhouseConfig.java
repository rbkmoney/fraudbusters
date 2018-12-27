package com.rbkmoney.fraudbusters.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.clickhouse.ClickHouseDataSource;

import javax.sql.DataSource;

@Configuration
public class ClickhouseConfig {

    @Value("${clickhouse.db.url}")
    private String DB_URL;

    @Bean
    public ClickHouseDataSource clickHouseDataSource() {
        return new ClickHouseDataSource(DB_URL);
    }

    @Bean
    @Autowired
    public JdbcTemplate jdbcTemplate(DataSource clickHouseDataSource) {
        return new JdbcTemplate(clickHouseDataSource);
    }
}
