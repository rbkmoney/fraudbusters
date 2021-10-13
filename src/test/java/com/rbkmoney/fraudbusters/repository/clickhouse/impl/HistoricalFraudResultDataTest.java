package com.rbkmoney.fraudbusters.repository.clickhouse.impl;

import com.rbkmoney.clickhouse.initializer.ChInitializer;
import com.rbkmoney.fraudbusters.config.ClickhouseConfig;
import com.rbkmoney.fraudbusters.constant.PaymentField;
import com.rbkmoney.fraudbusters.constant.SortOrder;
import com.rbkmoney.fraudbusters.domain.Event;
import com.rbkmoney.fraudbusters.repository.Repository;
import com.rbkmoney.fraudbusters.repository.clickhouse.mapper.EventMapper;
import com.rbkmoney.fraudbusters.service.dto.FilterDto;
import com.rbkmoney.fraudbusters.service.dto.SortDto;
import com.rbkmoney.fraudbusters.util.PaymentTypeByContextResolver;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@Testcontainers
@DataJdbcTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {ClickhouseConfig.class, PaymentTypeByContextResolver.class,
        FraudResultRepository.class, EventMapper.class, AggregationGeneralRepositoryImpl.class},
        initializers = HistoricalFraudResultDataTest.Initializer.class)
class HistoricalFraudResultDataTest {

    @Autowired
    private Repository<Event> fraudResultRepository;

    @Container
    public static ClickHouseContainer clickHouseContainer =
            new ClickHouseContainer("yandex/clickhouse-server:19.17");


    @Test
    void getFraudResultsByTimeSlot() {
        FilterDto filter = new FilterDto();
        filter.setTimeFrom("2020-05-01T18:04:53");
        filter.setTimeTo("2020-10-01T18:04:53");
        SortDto sortDto = new SortDto();
        sortDto.setOrder(SortOrder.DESC);
        filter.setSort(sortDto);

        List<Event> fraudResults = fraudResultRepository.getByFilter(filter);

        assertFalse(fraudResults.isEmpty());
        assertEquals(6, fraudResults.size());
    }

    @Test
    void getFraudResultsByTimeSlotAndSearchPatterns() {
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

        List<Event> fraudResults = fraudResultRepository.getByFilter(filter);

        assertFalse(fraudResults.isEmpty());
        assertEquals(1, fraudResults.size());
        assertEquals("2035728", fraudResults.get(0).getShopId());
        assertEquals("partyId_2", fraudResults.get(0).getPartyId());
    }

    @Test
    void getFraudResultsByTimeSlotAndLimitSize() {
        FilterDto filter = new FilterDto();
        filter.setTimeFrom("2020-05-01T18:04:53");
        filter.setTimeTo("2020-10-01T18:04:53");
        filter.setSize(3L);
        SortDto sortDto = new SortDto();
        sortDto.setOrder(SortOrder.DESC);
        filter.setSort(sortDto);

        List<Event> fraudResults = fraudResultRepository.getByFilter(filter);

        assertFalse(fraudResults.isEmpty());
        assertEquals(3, fraudResults.size());
    }

    @Test
    void getFraudResultsByTimeSlotAndPageAndSort() {
        FilterDto filter = new FilterDto();
        filter.setTimeFrom("2020-05-01T18:04:53");
        filter.setTimeTo("2020-10-01T18:04:53");
        filter.setLastId("1DkraVdGJfs.1");
        SortDto sortDto = new SortDto();
        sortDto.setOrder(SortOrder.DESC);
        filter.setSort(sortDto);

        List<Event> fraudResults = fraudResultRepository.getByFilter(filter);

        assertFalse(fraudResults.isEmpty());
        assertEquals(3, fraudResults.size());
    }

    @Test
    void getFraudResultsByTimeSlotAndPageAndSearchPatternsAndSort() {
        FilterDto filter = new FilterDto();
        filter.setTimeFrom("2020-05-01T18:04:53");
        filter.setTimeTo("2020-10-01T18:04:53");
        filter.setSize(3L);
        filter.setLastId("1DkraVdGJfs.1");
        SortDto sortDto = new SortDto();
        sortDto.setOrder(SortOrder.DESC);
        filter.setSort(sortDto);
        Map<PaymentField, String> patterns = new HashMap<>();
        patterns.put(PaymentField.PARTY_ID, "partyId_2");
        filter.setSearchPatterns(patterns);

        List<Event> fraudResults = fraudResultRepository.getByFilter(filter);

        assertFalse(fraudResults.isEmpty());
        assertEquals(2, fraudResults.size());
        assertEquals("partyId_2", fraudResults.get(0).getPartyId());
        assertEquals("partyId_2", fraudResults.get(1).getPartyId());
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
                    "sql/data/insert_history_fraud_results.sql"
            ));
        }
    }
}