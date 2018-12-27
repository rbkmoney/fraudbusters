package com.rbkmoney.fraudbusters.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.yandex.clickhouse.ClickHouseDataSource;

@Configuration
public class ClickhouseConfig {

    @Value("${clickhouse.db.url}")
    private String DB_URL;

    @Bean
    public ClickHouseDataSource clickHouseDataSource() {
        return new ClickHouseDataSource(DB_URL);
    }

}
