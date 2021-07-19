package com.rbkmoney.fraudbusters.repository.impl;

import com.rbkmoney.clickhouse.initializer.ChInitializer;
import com.rbkmoney.fraudbusters.config.ClickhouseConfig;
import com.rbkmoney.fraudbusters.domain.CheckedPayment;
import com.rbkmoney.fraudbusters.repository.HistoricalDataRepository;
import com.rbkmoney.fraudbusters.repository.mapper.CheckedPaymentMapper;
import com.rbkmoney.fraudbusters.service.dto.FilterDto;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.containers.ClickHouseContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@Testcontainers
@DataJdbcTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {ClickhouseConfig.class, CheckedPaymentMapper.class,
        HistoricalDataRepositoryImpl.class}, initializers = HistoricalDataRepositoryImplTest.Initializer.class)
class HistoricalDataRepositoryImplTest {

    @Autowired
    private HistoricalDataRepository historicalDataRepository;

    @Container
    public static ClickHouseContainer clickHouseContainer =
            new ClickHouseContainer("yandex/clickhouse-server:19.17");


    @Test
    void getPaymentsByTimeSlot() {
        FilterDto filter = new FilterDto();
        filter.setTimeFrom("2020-05-01T18:04:53");
        filter.setTimeTo("2020-10-01T18:04:53");
        filter.setSize(10);
        List<CheckedPayment> payments = historicalDataRepository.getPayments(filter);
        assertFalse(payments.isEmpty());
        assertEquals(6, payments.size());
    }


    public static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        @SneakyThrows
        @Override
        public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
            TestPropertyValues
                    .of(
                            "clickhouse.db.url=" + clickHouseContainer.getJdbcUrl(),
                            "clickhouse.db.user=" + clickHouseContainer.getUsername(),
                            "clickhouse.db.password=" + clickHouseContainer.getPassword()
                    )
                    .applyTo(configurableApplicationContext.getEnvironment());
            ChInitializer.initAllScripts(clickHouseContainer, List.of(
                    "sql/db_init.sql",
                    "sql/V4__create_payment.sql",
                    "sql/V5__add_fields.sql",
                    "sql/V6__add_result_fields_payment.sql",
                    "sql/V7__add_fields.sql",
                    "sql/data/insert_history_payments.sql"
            ));
        }
    }
}