package com.rbkmoney.fraudbusters.repository.impl;

import com.rbkmoney.damsel.fraudbusters.Refund;
import com.rbkmoney.fraudbusters.config.ClickhouseConfig;
import com.rbkmoney.fraudbusters.constant.PaymentField;
import com.rbkmoney.fraudbusters.constant.SortOrder;
import com.rbkmoney.fraudbusters.repository.Repository;
import com.rbkmoney.fraudbusters.repository.mapper.RefundMapper;
import com.rbkmoney.fraudbusters.service.dto.FilterDto;
import com.rbkmoney.fraudbusters.service.dto.SortDto;
import com.rbkmoney.fraudbusters.util.PaymentTypeByContextResolver;
import com.rbkmoney.testcontainers.annotations.clickhouse.ClickhouseTestcontainer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.test.context.ContextConfiguration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@ClickhouseTestcontainer(
        migrations = {
                "sql/db_init.sql",
                "sql/V4__create_payment.sql",
                "sql/V5__add_fields.sql",
                "sql/V6__add_result_fields_payment.sql",
                "sql/V7__add_fields.sql",
                "sql/data/insert_history_refunds.sql"})
@DataJdbcTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ContextConfiguration(
        classes = {
                ClickhouseConfig.class,
                PaymentTypeByContextResolver.class,
                RefundRepository.class,
                RefundMapper.class,
                AggregationStatusGeneralRepositoryImpl.class})
class HistoricalRefundDataTest {

    @Autowired
    private Repository<Refund> refundRepository;

    @Test
    void getRefundsByTimeSlot() {
        FilterDto filter = new FilterDto();
        filter.setTimeFrom("2020-05-01T18:04:53");
        filter.setTimeTo("2020-10-01T18:04:53");
        SortDto sortDto = new SortDto();
        sortDto.setOrder(SortOrder.DESC);
        filter.setSort(sortDto);

        List<Refund> refunds = refundRepository.getByFilter(filter);

        assertFalse(refunds.isEmpty());
        assertEquals(6, refunds.size());
    }

    @Test
    void getRefundsByTimeSlotAndSearchPatterns() {
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

        List<Refund> refunds = refundRepository.getByFilter(filter);

        assertFalse(refunds.isEmpty());
        assertEquals(1, refunds.size());
        assertEquals("2035728", refunds.get(0).getReferenceInfo().getMerchantInfo().getShopId());
        assertEquals("partyId_2", refunds.get(0).getReferenceInfo().getMerchantInfo().getPartyId());
    }

    @Test
    void getRefundsByTimeSlotAndLimitSize() {
        FilterDto filter = new FilterDto();
        filter.setTimeFrom("2020-05-01T18:04:53");
        filter.setTimeTo("2020-10-01T18:04:53");
        filter.setSize(3L);
        SortDto sortDto = new SortDto();
        sortDto.setOrder(SortOrder.DESC);
        filter.setSort(sortDto);

        List<Refund> refunds = refundRepository.getByFilter(filter);

        assertFalse(refunds.isEmpty());
        assertEquals(3, refunds.size());
    }

    @Test
    void getRefundsByTimeSlotAndPageAndSort() {
        FilterDto filter = new FilterDto();
        filter.setTimeFrom("2020-05-01T18:04:53");
        filter.setTimeTo("2020-10-01T18:04:53");
        filter.setLastId("1DkraVdGJfs.1|failed");
        SortDto sortDto = new SortDto();
        sortDto.setOrder(SortOrder.DESC);
        filter.setSort(sortDto);

        List<Refund> refunds = refundRepository.getByFilter(filter);

        assertFalse(refunds.isEmpty());
        assertEquals(3, refunds.size());
    }

    @Test
    void getRefundsByTimeSlotAndPageAndSearchPatternsAndSort() {
        FilterDto filter = new FilterDto();
        filter.setTimeFrom("2020-05-01T18:04:53");
        filter.setTimeTo("2020-10-01T18:04:53");
        filter.setSize(3L);
        filter.setLastId("1DkraVdGJfs.1|failed");
        SortDto sortDto = new SortDto();
        sortDto.setOrder(SortOrder.DESC);
        filter.setSort(sortDto);
        Map<PaymentField, String> patterns = new HashMap<>();
        patterns.put(PaymentField.PARTY_ID, "partyId_2");
        filter.setSearchPatterns(patterns);

        List<Refund> refunds = refundRepository.getByFilter(filter);

        assertFalse(refunds.isEmpty());
        assertEquals(2, refunds.size());
        assertEquals("partyId_2", refunds.get(0).getReferenceInfo().getMerchantInfo().getPartyId());
        assertEquals("partyId_2", refunds.get(1).getReferenceInfo().getMerchantInfo().getPartyId());
    }
}