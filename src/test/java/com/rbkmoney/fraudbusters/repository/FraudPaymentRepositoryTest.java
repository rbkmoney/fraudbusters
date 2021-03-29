package com.rbkmoney.fraudbusters.repository;

import com.rbkmoney.clickhouse.initializer.ChInitializer;
import com.rbkmoney.damsel.fraudbusters.FraudPayment;
import com.rbkmoney.damsel.fraudbusters.PaymentStatus;
import com.rbkmoney.damsel.geo_ip.GeoIpServiceSrv;
import com.rbkmoney.fraudbusters.config.ClickhouseConfig;
import com.rbkmoney.fraudbusters.domain.FraudPaymentRow;
import com.rbkmoney.fraudbusters.domain.TimeProperties;
import com.rbkmoney.fraudbusters.fraud.payment.resolver.DatabasePaymentFieldResolver;
import com.rbkmoney.fraudbusters.repository.impl.AggregationGeneralRepositoryImpl;
import com.rbkmoney.fraudbusters.repository.impl.AggregationStatusGeneralRepositoryImpl;
import com.rbkmoney.fraudbusters.repository.impl.FraudPaymentRepository;
import com.rbkmoney.fraudbusters.util.TimestampUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.testcontainers.containers.ClickHouseContainer;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.junit.Assert.assertEquals;

@Slf4j
@RunWith(SpringRunner.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@ContextConfiguration(classes = {ClickhouseConfig.class,
        DatabasePaymentFieldResolver.class,
        AggregationGeneralRepositoryImpl.class,
        AggregationStatusGeneralRepositoryImpl.class,
        FraudPaymentRepository.class
},
        initializers = FraudPaymentRepositoryTest.Initializer.class)
public class FraudPaymentRepositoryTest {

    private static final String SELECT_COUNT_AS_CNT_FROM_FRAUD_EVENTS_UNIQUE =
            "SELECT count() as cnt from fraud.fraud_payment";

    @ClassRule
    public static ClickHouseContainer clickHouseContainer =
            new ClickHouseContainer("yandex/clickhouse-server:19.17");
    @Autowired
    DatabasePaymentFieldResolver databasePaymentFieldResolver;
    @MockBean
    GeoIpServiceSrv.Iface iface;
    @Autowired
    private FraudPaymentRepository fraudPaymentRepository;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @NotNull
    public static FraudPaymentRow createFraudPaymentRow(String id) {
        FraudPaymentRow fraudPaymentRow = new FraudPaymentRow();
        fraudPaymentRow.setId(id);
        TimeProperties timeProperties = TimestampUtil.generateTimeProperties();
        fraudPaymentRow.setTimestamp(timeProperties.getTimestamp());
        fraudPaymentRow.setEventTimeHour(timeProperties.getEventTimeHour());
        fraudPaymentRow.setEventTime(timeProperties.getEventTime());
        fraudPaymentRow.setComment("");
        fraudPaymentRow.setType("Card not present");
        fraudPaymentRow.setPaymentStatus(PaymentStatus.captured.name());
        return fraudPaymentRow;
    }

    public static FraudPayment createFraudPayment(String id) {
        return new FraudPayment()
                .setId(id)
                .setEventTime(LocalDateTime.now()
                        .format(DateTimeFormatter.ofPattern(TimestampUtil.YYYY_MM_DD_HH_MM_SS)))
                .setComment("")
                .setType("Card not present");
    }

    @Test
    public void insertBatch() throws SQLException {
        fraudPaymentRepository.insertBatch(createBatch());

        Integer count = jdbcTemplate.queryForObject(
                SELECT_COUNT_AS_CNT_FROM_FRAUD_EVENTS_UNIQUE,
                (resultSet, i) -> resultSet.getInt("cnt")
        );

        assertEquals(2, count.intValue());
    }

    @NotNull
    private List<FraudPaymentRow> createBatch() {
        FraudPaymentRow value = createFraudPaymentRow("inv1.1");
        FraudPaymentRow value2 = createFraudPaymentRow("inv2.1");
        return List.of(value, value2);
    }

    public static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        @SneakyThrows
        @Override
        public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
            log.info("clickhouse.db.url={}", clickHouseContainer.getJdbcUrl());
            TestPropertyValues
                    .of(
                            "clickhouse.db.url=" + clickHouseContainer.getJdbcUrl(),
                            "clickhouse.db.user=" + clickHouseContainer.getUsername(),
                            "clickhouse.db.password=" + clickHouseContainer.getPassword()
                    )
                    .applyTo(configurableApplicationContext.getEnvironment());

            ChInitializer.initAllScripts(
                    clickHouseContainer,
                    List.of("sql/V3__create_fraud_payments.sql")
            );
        }
    }
}
