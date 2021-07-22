package com.rbkmoney.fraudbusters.converter;

import com.rbkmoney.damsel.fraudbusters.Filter;
import com.rbkmoney.damsel.fraudbusters.Page;
import com.rbkmoney.damsel.fraudbusters.Sort;
import com.rbkmoney.damsel.fraudbusters.SortOrder;
import com.rbkmoney.fraudbusters.TestObjectsFactory;
import com.rbkmoney.fraudbusters.constant.PaymentField;
import com.rbkmoney.fraudbusters.service.dto.FilterDto;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;


class FilterConverterTest {

    private final FilterConverter filterConverter = new FilterConverter();

    @Test
    void convertWithEmptyFiled() {
        Filter filter = new Filter();
        filter.setPaymentId("");
        filter.setTerminal(null);
        Page page = new Page();
        Sort sort = new Sort();

        FilterDto dto = filterConverter.convert(filter, page, sort);

        assertTrue(dto.getSearchPatterns().isEmpty());
        assertEquals(10L, dto.getSize());
        assertNull(dto.getLastId());
        assertNull(dto.getSort().getOrder());
        assertNull(dto.getSort().getField());
    }

    @Test
    void convert() {
        Filter filter = TestObjectsFactory.testFilter();
        Page page = TestObjectsFactory.testPage();
        Sort sort = TestObjectsFactory.testSort();

        FilterDto dto = filterConverter.convert(filter, page, sort);

        assertEquals(page.getSize(), dto.getSize());
        assertEquals(page.getContinuationId(), dto.getLastId());
        assertEquals(filter.getInterval().getLowerBound().getBoundTime(), dto.getTimeFrom());
        assertEquals(filter.getInterval().getUpperBound().getBoundTime(), dto.getTimeTo());
        Map<PaymentField, String> searchPatterns = dto.getSearchPatterns();
        assertEquals(filter.getCardToken(), searchPatterns.get(PaymentField.CARD_TOKEN));
        assertEquals(filter.getEmail(), searchPatterns.get(PaymentField.EMAIL));
        assertEquals(filter.getFingerprint(), searchPatterns.get(PaymentField.FINGERPRINT));
        assertEquals(filter.getPartyId(), searchPatterns.get(PaymentField.PARTY_ID));
        assertEquals(filter.getShopId(), searchPatterns.get(PaymentField.SHOP_ID));
        assertEquals(filter.getStatus(), searchPatterns.get(PaymentField.STATUS));
        assertEquals(filter.getProviderCountry(), searchPatterns.get(PaymentField.BANK_COUNTRY));
        assertEquals(filter.getTerminal(), searchPatterns.get(PaymentField.TERMINAL));
        assertEquals(filter.getPaymentId(), searchPatterns.get(PaymentField.ID));
        assertEquals(sort.getField(), dto.getSort().getField());
        assertEquals(sort.getOrder(), SortOrder.valueOf(dto.getSort().getOrder().name()));
    }
}