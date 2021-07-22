package com.rbkmoney.fraudbusters.service;

import com.rbkmoney.fraudbusters.TestObjectsFactory;
import com.rbkmoney.fraudbusters.domain.CheckedPayment;
import com.rbkmoney.fraudbusters.repository.Repository;
import com.rbkmoney.fraudbusters.service.dto.FilterDto;
import com.rbkmoney.fraudbusters.service.dto.HistoricalPaymentsDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {HistoricalDataServiceImpl.class})
class HistoricalDataServiceImplTest {

    @Autowired
    private HistoricalDataService historicalDataService;

    @MockBean
    private Repository<CheckedPayment> paymentRepository;

    @Test
    void getPaymentsWithoutPayments() {
        FilterDto filterDto = new FilterDto();
        when(paymentRepository.getByFilter(filterDto)).thenReturn(Collections.emptyList());

        HistoricalPaymentsDto actualPayments = historicalDataService.getPayments(filterDto);

        assertNull(actualPayments.getLastId());
        assertTrue(actualPayments.getPayments().isEmpty());
    }

    @Test
    void getPaymentsWithoutLastId() {
        FilterDto filterDto = new FilterDto();
        CheckedPayment checkedPayment = TestObjectsFactory.testCheckedPayment();
        List<CheckedPayment> checkedPayments = List.of(checkedPayment);
        when(paymentRepository.getByFilter(filterDto)).thenReturn(checkedPayments);

        HistoricalPaymentsDto actualPayments = historicalDataService.getPayments(filterDto);

        assertNull(actualPayments.getLastId());
        assertFalse(actualPayments.getPayments().isEmpty());
        assertEquals(checkedPayments.size(), actualPayments.getPayments().size());
        CheckedPayment actualPayment = actualPayments.getPayments().get(0);
        assertEquals(checkedPayment.getPaymentStatus(), actualPayment.getPaymentStatus());
        assertEquals(checkedPayment.getPaymentTool(), actualPayment.getPaymentTool());
        assertEquals(checkedPayment.getAmount(), actualPayment.getAmount());
        assertEquals(checkedPayment.getPaymentCountry(), actualPayment.getPaymentCountry());
        assertEquals(checkedPayment.getPartyId(), actualPayment.getPartyId());
        assertEquals(checkedPayment.getShopId(), actualPayment.getShopId());
        assertEquals(checkedPayment.getFingerprint(), actualPayment.getFingerprint());
        assertEquals(checkedPayment.getCardToken(), actualPayment.getCardToken());
        assertEquals(checkedPayment.getCurrency(), actualPayment.getCurrency());
        assertEquals(checkedPayment.getEmail(), actualPayment.getEmail());
        assertEquals(checkedPayment.getBankCountry(), actualPayment.getBankCountry());
        assertEquals(checkedPayment.getId(), actualPayment.getId());
        assertEquals(checkedPayment.getIp(), actualPayment.getIp());
        assertEquals(checkedPayment.getTerminal(), actualPayment.getTerminal());
        assertEquals(checkedPayment.getProviderId(), actualPayment.getProviderId());
        assertEquals(checkedPayment.getPaymentSystem(), actualPayment.getPaymentSystem());
        assertEquals(checkedPayment.getEventTime(), actualPayment.getEventTime());

    }

    @Test
    void getPaymentsWithLastId() {
        FilterDto filterDto = new FilterDto();
        List<CheckedPayment> checkedPayments = TestObjectsFactory.testCheckedPayments(4);
        filterDto.setSize((long) checkedPayments.size());
        when(paymentRepository.getByFilter(filterDto)).thenReturn(checkedPayments);

        HistoricalPaymentsDto actualPayments = historicalDataService.getPayments(filterDto);

        String expectedLastId = checkedPayments.get(3).getId() + "|" + checkedPayments.get(3).getPaymentStatus();
        assertEquals(expectedLastId, actualPayments.getLastId());
        assertFalse(actualPayments.getPayments().isEmpty());
        assertEquals(checkedPayments.size(), actualPayments.getPayments().size());
    }
}