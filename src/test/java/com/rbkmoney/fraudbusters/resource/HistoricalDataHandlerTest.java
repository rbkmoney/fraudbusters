package com.rbkmoney.fraudbusters.resource;

import com.rbkmoney.damsel.fraudbusters.*;
import com.rbkmoney.fraudbusters.TestObjectsFactory;
import com.rbkmoney.fraudbusters.constant.PaymentToolType;
import com.rbkmoney.fraudbusters.converter.CheckedPaymentToPaymentConverter;
import com.rbkmoney.fraudbusters.converter.FilterConverter;
import com.rbkmoney.fraudbusters.converter.HistoricalDataResponseConverter;
import com.rbkmoney.fraudbusters.domain.CheckedPayment;
import com.rbkmoney.fraudbusters.resource.payment.handler.HistoricalDataHandler;
import com.rbkmoney.fraudbusters.service.HistoricalDataService;
import com.rbkmoney.fraudbusters.service.dto.FilterDto;
import com.rbkmoney.fraudbusters.service.dto.HistoricalPaymentsDto;
import com.rbkmoney.geck.common.util.TBaseUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.Instant;
import java.time.ZoneId;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {HistoricalDataHandler.class, CheckedPaymentToPaymentConverter.class,
        FilterConverter.class, HistoricalDataResponseConverter.class})
class HistoricalDataHandlerTest {

    @Autowired
    private HistoricalDataHandler handler;

    @MockBean
    private HistoricalDataService service;


    @Test
    void getPaymentsWithoutPayments() {
        Filter filter = new Filter();
        Page page = new Page();
        Sort sort = new Sort();
        sort.setOrder(SortOrder.DESC);
        HistoricalPaymentsDto dto = HistoricalPaymentsDto.builder()
                .payments(Collections.emptyList())
                .lastId(null)
                .build();
        when(service.getPayments(any(FilterDto.class))).thenReturn(dto);

        HistoricalDataResponse actualResponse = handler.getPayments(filter, page, sort);

        assertNull(actualResponse.getContinuationId());
        assertTrue(actualResponse.getData().getPayments().isEmpty());
    }

    @Test
    void getPaymentsWithoutLastId() {
        Filter filter = new Filter();
        Page page = new Page();
        Sort sort = new Sort();
        sort.setOrder(SortOrder.DESC);
        CheckedPayment checkedPayment = TestObjectsFactory.testCheckedPayment();
        HistoricalPaymentsDto dto = HistoricalPaymentsDto.builder()
                .payments(List.of(checkedPayment))
                .lastId(null)
                .build();
        when(service.getPayments(any(FilterDto.class))).thenReturn(dto);

        HistoricalDataResponse actualResponse = handler.getPayments(filter, page, sort);

        assertNull(actualResponse.getContinuationId());
        assertFalse(actualResponse.getData().getPayments().isEmpty());
        Payment actualPayment = actualResponse.getData().getPayments().get(0);
        assertEquals("bank_card",
                TBaseUtil.unionFieldToEnum(actualPayment.getPaymentTool(), PaymentToolType.class).name());
        assertEquals(checkedPayment.getPaymentSystem(),
                actualPayment.getPaymentTool().getBankCard().getPaymentSystem().getId());
        assertEquals(checkedPayment.getCardToken(), actualPayment.getPaymentTool().getBankCard().getToken());
        assertEquals(checkedPayment.getIp(), actualPayment.getClientInfo().getIp());
        assertEquals(checkedPayment.getEmail(), actualPayment.getClientInfo().getEmail());
        assertEquals(checkedPayment.getFingerprint(), actualPayment.getClientInfo().getFingerprint());
        assertEquals(checkedPayment.getPartyId(), actualPayment.getReferenceInfo().getMerchantInfo().getPartyId());
        assertEquals(checkedPayment.getShopId(), actualPayment.getReferenceInfo().getMerchantInfo().getShopId());
        assertEquals(checkedPayment.getProviderId(), actualPayment.getProviderInfo().getProviderId());
        assertEquals(checkedPayment.getTerminal(), actualPayment.getProviderInfo().getTerminalId());
        assertEquals(checkedPayment.getBankCountry(), actualPayment.getProviderInfo().getCountry());
        assertEquals(checkedPayment.getPaymentStatus(), actualPayment.getStatus().toString());
        assertEquals(checkedPayment.getAmount(), actualPayment.getCost().getAmount());
        assertEquals(checkedPayment.getCurrency(), actualPayment.getCost().getCurrency().getSymbolicCode());
        assertEquals(Instant.ofEpochMilli(checkedPayment.getEventTime()).atZone(ZoneId.of("UTC")).toLocalDateTime()
                        .toString(),
                actualPayment.getEventTime());
        assertEquals(checkedPayment.getId(), actualPayment.getId());

    }

    @Test
    void getPayments() {
        Filter filter = new Filter();
        Page page = new Page();
        Sort sort = new Sort();
        sort.setOrder(SortOrder.DESC);
        String lastId = TestObjectsFactory.randomString();
        CheckedPayment checkedPayment = TestObjectsFactory.testCheckedPayment();
        List<CheckedPayment> checkedPayments = List.of(checkedPayment);
        HistoricalPaymentsDto dto = HistoricalPaymentsDto.builder()
                .payments(checkedPayments)
                .lastId(lastId)
                .build();
        when(service.getPayments(any(FilterDto.class))).thenReturn(dto);

        HistoricalDataResponse actualResponse = handler.getPayments(filter, page, sort);

        assertEquals(lastId, actualResponse.getContinuationId());
        assertEquals(checkedPayments.size(), actualResponse.getData().getPayments().size());

    }
}