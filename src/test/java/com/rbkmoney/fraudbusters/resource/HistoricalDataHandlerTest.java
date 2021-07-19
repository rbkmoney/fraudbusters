package com.rbkmoney.fraudbusters.resource;

import com.rbkmoney.damsel.fraudbusters.Filter;
import com.rbkmoney.damsel.fraudbusters.Page;
import com.rbkmoney.damsel.fraudbusters.PaymentInfo;
import com.rbkmoney.damsel.fraudbusters.PaymentInfoResult;
import com.rbkmoney.fraudbusters.TestObjectsFactory;
import com.rbkmoney.fraudbusters.converter.CheckedPaymentToPaymentInfoConverter;
import com.rbkmoney.fraudbusters.converter.FilterConverter;
import com.rbkmoney.fraudbusters.domain.CheckedPayment;
import com.rbkmoney.fraudbusters.service.HistoricalDataService;
import com.rbkmoney.fraudbusters.service.dto.FilterDto;
import com.rbkmoney.fraudbusters.service.dto.HistoricalPaymentsDto;
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
@SpringBootTest(classes = {HistoricalDataHandler.class, CheckedPaymentToPaymentInfoConverter.class,
        FilterConverter.class})
class HistoricalDataHandlerTest {

    @Autowired
    private HistoricalDataHandler handler;

    @MockBean
    private HistoricalDataService service;


    @Test
    void getPaymentsWithoutPayments() {
        Filter filter = new Filter();
        Page page = new Page();
        HistoricalPaymentsDto dto = HistoricalPaymentsDto.builder()
                .payments(Collections.emptyList())
                .lastId(null)
                .build();
        when(service.getPayments(any(FilterDto.class))).thenReturn(dto);

        PaymentInfoResult actualPayments = handler.getPayments(filter, page);

        assertNull(actualPayments.getContinuationId());
        assertTrue(actualPayments.getPayments().isEmpty());
    }

    @Test
    void getPaymentsWithoutLastId() {
        Filter filter = new Filter();
        Page page = new Page();
        CheckedPayment checkedPayment = TestObjectsFactory.testCheckedPayment();
        HistoricalPaymentsDto dto = HistoricalPaymentsDto.builder()
                .payments(List.of(checkedPayment))
                .lastId(null)
                .build();
        when(service.getPayments(any(FilterDto.class))).thenReturn(dto);

        PaymentInfoResult actualPayments = handler.getPayments(filter, page);

        assertNull(actualPayments.getContinuationId());
        assertFalse(actualPayments.getPayments().isEmpty());
        PaymentInfo actualPaymentInfo = actualPayments.getPayments().get(0);
        assertEquals(checkedPayment.getPaymentSystem(), actualPaymentInfo.getPaymentSystem());
        assertEquals(checkedPayment.getPaymentTool(), actualPaymentInfo.getPaymentTool());
        assertEquals(checkedPayment.getPaymentCountry(), actualPaymentInfo.getPaymentCountry());
        assertEquals(checkedPayment.getIp(), actualPaymentInfo.getClientInfo().getIp());
        assertEquals(checkedPayment.getEmail(), actualPaymentInfo.getClientInfo().getEmail());
        assertEquals(checkedPayment.getFingerprint(), actualPaymentInfo.getClientInfo().getFingerprint());
        assertEquals(checkedPayment.getPartyId(), actualPaymentInfo.getMerchantInfo().getPartyId());
        assertEquals(checkedPayment.getShopId(), actualPaymentInfo.getMerchantInfo().getShopId());
        assertEquals(checkedPayment.getProviderId(), actualPaymentInfo.getProvider().getProviderId());
        assertEquals(checkedPayment.getTerminal(), actualPaymentInfo.getProvider().getTerminalId());
        assertEquals(checkedPayment.getBankCountry(), actualPaymentInfo.getProvider().getCountry());
        assertEquals(checkedPayment.getPaymentStatus(), actualPaymentInfo.getStatus().toString());
        assertEquals(checkedPayment.getAmount(), actualPaymentInfo.getAmount());
        assertEquals(checkedPayment.getCardToken(), actualPaymentInfo.getCardToken());
        assertEquals(checkedPayment.getCurrency(), actualPaymentInfo.getCurrency());
        assertEquals(Instant.ofEpochMilli(checkedPayment.getEventTime()).atZone(ZoneId.of("UTC")).toLocalDateTime()
                        .toString(),
                actualPaymentInfo.getEventTime());
        assertEquals(checkedPayment.getId(), actualPaymentInfo.getId());

    }

    @Test
    void getPayments() {
        Filter filter = new Filter();
        Page page = new Page();
        String lastId = TestObjectsFactory.randomString();
        CheckedPayment checkedPayment = TestObjectsFactory.testCheckedPayment();
        List<CheckedPayment> checkedPayments = List.of(checkedPayment);
        HistoricalPaymentsDto dto = HistoricalPaymentsDto.builder()
                .payments(checkedPayments)
                .lastId(lastId)
                .build();
        when(service.getPayments(any(FilterDto.class))).thenReturn(dto);

        PaymentInfoResult actualPayments = handler.getPayments(filter, page);

        assertEquals(lastId, actualPayments.getContinuationId());
        assertEquals(checkedPayments.size(), actualPayments.getPaymentsSize());

    }
}