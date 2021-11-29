package com.rbkmoney.fraudbusters.repository.clickhouse.impl;

import com.rbkmoney.clickhouse.initializer.ChInitializer;
import com.rbkmoney.fraudbusters.config.ClickhouseConfig;
import com.rbkmoney.fraudbusters.constant.PaymentField;
import com.rbkmoney.fraudbusters.constant.SortOrder;
import com.rbkmoney.fraudbusters.domain.CheckedPayment;
import com.rbkmoney.fraudbusters.repository.Repository;
import com.rbkmoney.fraudbusters.repository.clickhouse.mapper.CheckedPaymentMapper;
import com.rbkmoney.fraudbusters.service.dto.FilterDto;
import com.rbkmoney.fraudbusters.service.dto.SortDto;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.containers.ClickHouseContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@ActiveProfiles("full-prod")
@Testcontainers
@DataJdbcTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {ClickhouseConfig.class, CheckedPaymentMapper.class,
        PaymentRepositoryImpl.class},
        initializers = HistoricalPaymentDataTest.Initializer.class)
class HistoricalPaymentDataTest {

    @Autowired
    private Repository<CheckedPayment> paymentRepository;

    @Container
    public static ClickHouseContainer clickHouseContainer =
            new ClickHouseContainer("yandex/clickhouse-server:19.17");


    @Test
    void getPaymentsByTimeSlot() {
        FilterDto filter = new FilterDto();
        filter.setTimeFrom("2020-05-01T18:04:53");
        filter.setTimeTo("2020-10-01T18:04:53");
        SortDto sortDto = new SortDto();
        sortDto.setOrder(SortOrder.DESC);
        filter.setSort(sortDto);

        List<CheckedPayment> payments = paymentRepository.getByFilter(filter);

        assertFalse(payments.isEmpty());
        assertEquals(6, payments.size());
    }

    @Test
    void getPaymentsByTimeSlotAndSearchPatterns() {
        FilterDto filter = new FilterDto();
        filter.setTimeFrom("2020-05-01T18:04:53");
        filter.setTimeTo("2020-10-01T18:04:53");
        Map<PaymentField, String> patterns = new HashMap<>();
        patterns.put(PaymentField.PARTY_ID, "partyId_2");
        patterns.put(PaymentField.SHOP_ID, "2035728");
        filter.setSearchPatterns(patterns);
        SortDto sortDto = new SortDto();
        sortDto.setOrder(SortOrder.DESC);
        filter.setSort(sortDto);

        List<CheckedPayment> payments = paymentRepository.getByFilter(filter);

        assertFalse(payments.isEmpty());
        assertEquals(1, payments.size());
        assertEquals("2035728", payments.get(0).getShopId());
        assertEquals("partyId_2", payments.get(0).getPartyId());
    }

    @Test
    void getPaymentsByTimeSlotAndLimitSize() {
        FilterDto filter = new FilterDto();
        filter.setTimeFrom("2020-05-01T18:04:53");
        filter.setTimeTo("2020-10-01T18:04:53");
        filter.setSize(3L);
        SortDto sortDto = new SortDto();
        sortDto.setOrder(SortOrder.DESC);
        filter.setSort(sortDto);

        List<CheckedPayment> payments = paymentRepository.getByFilter(filter);

        assertFalse(payments.isEmpty());
        assertEquals(3, payments.size());
    }

    @Test
    void getPaymentsByTimeSlotAndPageAndSort() {
        FilterDto filter = new FilterDto();
        filter.setTimeFrom("2020-05-01T18:04:53");
        filter.setTimeTo("2020-10-01T18:04:53");
        filter.setLastId("1DkraVdGJfs.1|processed");
        SortDto sortDto = new SortDto();
        sortDto.setOrder(SortOrder.DESC);
        filter.setSort(sortDto);

        List<CheckedPayment> payments = paymentRepository.getByFilter(filter);

        assertFalse(payments.isEmpty());
        assertEquals(3, payments.size());
    }

    @Test
    void getPaymentsByTimeSlotAndPageAndSearchPatternsAndSort() {
        FilterDto filter = new FilterDto();
        filter.setTimeFrom("2020-05-01T18:04:53");
        filter.setTimeTo("2020-10-01T18:04:53");
        filter.setSize(3L);
        filter.setLastId("1DkraVdGJfs.1|processed");
        SortDto sortDto = new SortDto();
        sortDto.setOrder(SortOrder.DESC);
        filter.setSort(sortDto);
        Map<PaymentField, String> patterns = new HashMap<>();
        patterns.put(PaymentField.PARTY_ID, "partyId_2");
        filter.setSearchPatterns(patterns);

        List<CheckedPayment> payments = paymentRepository.getByFilter(filter);

        assertFalse(payments.isEmpty());
        assertEquals(1, payments.size());
        assertEquals("partyId_2", payments.get(0).getPartyId());
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
                    "sql/V3__create_fraud_payments.sql",
                    "sql/V4__create_payment.sql",
                    "sql/V5__add_fields.sql",
                    "sql/V6__add_result_fields_payment.sql",
                    "sql/V7__add_fields.sql",
                    "sql/V8__create_withdrawal.sql",
                    "sql/V9__add_phone_category_card.sql",
                    "sql/data/insert_history_payments.sql"
            ));
        }
    }
}
