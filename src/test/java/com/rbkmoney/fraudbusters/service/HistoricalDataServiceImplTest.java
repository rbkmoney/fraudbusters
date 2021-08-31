package com.rbkmoney.fraudbusters.service;

import com.rbkmoney.damsel.fraudbusters.Chargeback;
import com.rbkmoney.damsel.fraudbusters.Refund;
import com.rbkmoney.fraudbusters.TestObjectsFactory;
import com.rbkmoney.fraudbusters.domain.CheckedPayment;
import com.rbkmoney.fraudbusters.domain.Event;
import com.rbkmoney.fraudbusters.domain.FraudPaymentRow;
import com.rbkmoney.fraudbusters.repository.Repository;
import com.rbkmoney.fraudbusters.service.dto.*;
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
    @MockBean
    private Repository<Refund> refundRepository;
    @MockBean
    private Repository<Chargeback> chargebackRepository;
    @MockBean
    private Repository<Event> fraudResultRepository;
    @MockBean
    private Repository<FraudPaymentRow> fraudPaymentRepository;

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

    @Test
    void getRefundsWithoutRefunds() {
        FilterDto filterDto = new FilterDto();
        when(refundRepository.getByFilter(filterDto)).thenReturn(Collections.emptyList());

        HistoricalRefundsDto actualRefunds = historicalDataService.getRefunds(filterDto);

        assertNull(actualRefunds.getLastId());
        assertTrue(actualRefunds.getRefunds().isEmpty());
    }

    @Test
    void getRefundsWithoutLastId() {
        FilterDto filterDto = new FilterDto();
        Refund refund = TestObjectsFactory.testRefund();
        List<Refund> refunds = List.of(refund);
        when(refundRepository.getByFilter(filterDto)).thenReturn(refunds);

        HistoricalRefundsDto actualRefunds = historicalDataService.getRefunds(filterDto);

        assertNull(actualRefunds.getLastId());
        assertFalse(actualRefunds.getRefunds().isEmpty());
        assertIterableEquals(refunds, actualRefunds.getRefunds());
    }

    @Test
    void getRefundsWithLastId() {
        FilterDto filterDto = new FilterDto();
        List<Refund> refunds = TestObjectsFactory.testRefunds(4);
        filterDto.setSize((long) refunds.size());
        when(refundRepository.getByFilter(filterDto)).thenReturn(refunds);

        HistoricalRefundsDto actualRefunds = historicalDataService.getRefunds(filterDto);

        String expectedLastId = refunds.get(3).getId() + "|" + refunds.get(3).getStatus();
        assertEquals(expectedLastId, actualRefunds.getLastId());
        assertFalse(actualRefunds.getRefunds().isEmpty());
        assertEquals(refunds.size(), actualRefunds.getRefunds().size());
    }

    @Test
    void getChargebacksWithoutChargebacks() {
        FilterDto filterDto = new FilterDto();
        when(chargebackRepository.getByFilter(filterDto)).thenReturn(Collections.emptyList());

        HistoricalChargebacksDto actualChargebacks = historicalDataService.getChargebacks(filterDto);

        assertNull(actualChargebacks.getLastId());
        assertTrue(actualChargebacks.getChargebacks().isEmpty());
    }

    @Test
    void getChargebacksWithoutLastId() {
        FilterDto filterDto = new FilterDto();
        Chargeback chargeback = TestObjectsFactory.testChargeback();
        List<Chargeback> chargebacks = List.of(chargeback);
        when(chargebackRepository.getByFilter(filterDto)).thenReturn(chargebacks);

        HistoricalChargebacksDto actualChargebacks = historicalDataService.getChargebacks(filterDto);

        assertNull(actualChargebacks.getLastId());
        assertFalse(actualChargebacks.getChargebacks().isEmpty());
        assertIterableEquals(chargebacks, actualChargebacks.getChargebacks());
    }

    @Test
    void getChargebacksWithLastId() {
        FilterDto filterDto = new FilterDto();
        List<Chargeback> chargebacks = TestObjectsFactory.testChargebacks(4);
        filterDto.setSize((long) chargebacks.size());
        when(chargebackRepository.getByFilter(filterDto)).thenReturn(chargebacks);

        HistoricalChargebacksDto actualChargebacks = historicalDataService.getChargebacks(filterDto);

        String expectedLastId = chargebacks.get(3).getId() + "|" + chargebacks.get(3).getStatus();
        assertEquals(expectedLastId, actualChargebacks.getLastId());
        assertFalse(actualChargebacks.getChargebacks().isEmpty());
        assertEquals(chargebacks.size(), actualChargebacks.getChargebacks().size());
        assertIterableEquals(chargebacks, actualChargebacks.getChargebacks());
    }

    @Test
    void getFraudResultsWithoutFraudResults() {
        FilterDto filterDto = new FilterDto();
        when(fraudResultRepository.getByFilter(filterDto)).thenReturn(Collections.emptyList());

        HistoricalFraudResultsDto actualFraudResults = historicalDataService.getFraudResults(filterDto);

        assertNull(actualFraudResults.getLastId());
        assertTrue(actualFraudResults.getFraudResults().isEmpty());
    }

    @Test
    void getFraudResultsWithoutLastId() {
        FilterDto filterDto = new FilterDto();
        Event event = TestObjectsFactory.testEvent();
        List<Event> events = List.of(event);
        when(fraudResultRepository.getByFilter(filterDto)).thenReturn(events);

        HistoricalFraudResultsDto actualFraudResults = historicalDataService.getFraudResults(filterDto);

        assertNull(actualFraudResults.getLastId());
        assertFalse(actualFraudResults.getFraudResults().isEmpty());
        assertEquals(events.size(), actualFraudResults.getFraudResults().size());
        assertIterableEquals(events, actualFraudResults.getFraudResults());

    }

    @Test
    void getFraudResultsWithLastId() {
        FilterDto filterDto = new FilterDto();
        List<Event> fraudResults = TestObjectsFactory.testEvents(4);
        filterDto.setSize((long) fraudResults.size());
        when(fraudResultRepository.getByFilter(filterDto)).thenReturn(fraudResults);

        HistoricalFraudResultsDto actualFraudResults = historicalDataService.getFraudResults(filterDto);

        assertEquals(fraudResults.get(3).getPaymentId(), actualFraudResults.getLastId());
        assertFalse(actualFraudResults.getFraudResults().isEmpty());
        assertEquals(fraudResults.size(), actualFraudResults.getFraudResults().size());
    }

    @Test
    void getFraudPaymentsWithoutFraudPayments() {
        FilterDto filterDto = new FilterDto();
        when(fraudPaymentRepository.getByFilter(filterDto)).thenReturn(Collections.emptyList());

        HistoricalPaymentsDto actualFraudPayments = historicalDataService.getFraudPayments(filterDto);

        assertNull(actualFraudPayments.getLastId());
        assertTrue(actualFraudPayments.getPayments().isEmpty());
    }

    @Test
    void getFraudPaymentsWithoutLastId() {
        FilterDto filterDto = new FilterDto();
        FraudPaymentRow fraudPaymentRow = TestObjectsFactory.testFraudPaymentRow();
        List<FraudPaymentRow> fraudPaymentRows = List.of(fraudPaymentRow);
        when(fraudPaymentRepository.getByFilter(filterDto)).thenReturn(fraudPaymentRows);

        HistoricalPaymentsDto actualFraudPayments = historicalDataService.getFraudPayments(filterDto);

        assertNull(actualFraudPayments.getLastId());
        assertFalse(actualFraudPayments.getPayments().isEmpty());
        assertEquals(fraudPaymentRows.size(), actualFraudPayments.getPayments().size());
        assertIterableEquals(fraudPaymentRows, actualFraudPayments.getPayments());

    }

    @Test
    void getFraudPaymentsWithLastId() {
        FilterDto filterDto = new FilterDto();
        List<FraudPaymentRow> fraudPaymentRows = TestObjectsFactory.testFraudPaymentRows(4);
        filterDto.setSize((long) fraudPaymentRows.size());
        when(fraudPaymentRepository.getByFilter(filterDto)).thenReturn(fraudPaymentRows);

        HistoricalPaymentsDto actualFraudPayments = historicalDataService.getFraudPayments(filterDto);

        String expectedLastId = fraudPaymentRows.get(3).getId() + "|" + fraudPaymentRows.get(3).getPaymentStatus();
        assertEquals(expectedLastId, actualFraudPayments.getLastId());
        assertFalse(actualFraudPayments.getPayments().isEmpty());
        assertEquals(fraudPaymentRows.size(), actualFraudPayments.getPayments().size());
    }

}
