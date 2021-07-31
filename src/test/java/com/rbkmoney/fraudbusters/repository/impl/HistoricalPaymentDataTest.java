package com.rbkmoney.fraudbusters.repository.impl;

import com.rbkmoney.fraudbusters.config.ClickhouseConfig;
import com.rbkmoney.fraudbusters.constant.PaymentField;
import com.rbkmoney.fraudbusters.constant.SortOrder;
import com.rbkmoney.fraudbusters.domain.CheckedPayment;
import com.rbkmoney.fraudbusters.repository.Repository;
import com.rbkmoney.fraudbusters.repository.mapper.CheckedPaymentMapper;
import com.rbkmoney.fraudbusters.service.dto.FilterDto;
import com.rbkmoney.fraudbusters.service.dto.SortDto;
import com.rbkmoney.testcontainers.annotations.clickhouse.ClickhouseTestcontainer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@ActiveProfiles("full-prod")
@ClickhouseTestcontainer(
        migrations = {
                "sql/db_init.sql",
                "sql/V4__create_payment.sql",
                "sql/V5__add_fields.sql",
                "sql/V6__add_result_fields_payment.sql",
                "sql/V7__add_fields.sql",
                "sql/data/insert_history_payments.sql"})
@DataJdbcTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ContextConfiguration(
        classes = {
                ClickhouseConfig.class,
                CheckedPaymentMapper.class,
                PaymentRepositoryImpl.class})
class HistoricalPaymentDataTest {

    @Autowired
    private Repository<CheckedPayment> paymentRepository;

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
}
