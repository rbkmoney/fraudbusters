package com.rbkmoney.fraudbusters.resource.payment.handler;

import com.rbkmoney.damsel.fraudbusters.*;
import com.rbkmoney.fraudbusters.TestObjectsFactory;
import com.rbkmoney.fraudbusters.constant.PaymentToolType;
import com.rbkmoney.fraudbusters.converter.*;
import com.rbkmoney.fraudbusters.domain.CheckedPayment;
import com.rbkmoney.fraudbusters.domain.Event;
import com.rbkmoney.fraudbusters.domain.FraudPaymentRow;
import com.rbkmoney.fraudbusters.service.HistoricalDataService;
import com.rbkmoney.fraudbusters.service.dto.*;
import com.rbkmoney.geck.common.util.TBaseUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {HistoricalDataHandler.class, CheckedPaymentToPaymentConverter.class,
        FilterConverter.class, HistoricalDataResponseConverter.class, EventToHistoricalTransactionCheckConverter.class,
        CheckedPaymentToFraudPaymentInfoConverter.class, ResultStatusConverter.class})
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
        assertEquals(LocalDateTime.ofInstant(
                Instant.ofEpochSecond(checkedPayment.getEventTime()), ZoneId.of("UTC"))
                        .format(DateTimeFormatter.ISO_DATE_TIME),
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

    @Test
    void getRefundsWithoutRefunds() {
        Filter filter = new Filter();
        Page page = new Page();
        Sort sort = new Sort();
        sort.setOrder(SortOrder.DESC);
        HistoricalRefundsDto dto = HistoricalRefundsDto.builder()
                .refunds(Collections.emptyList())
                .lastId(null)
                .build();
        when(service.getRefunds(any(FilterDto.class))).thenReturn(dto);

        HistoricalDataResponse actualResponse = handler.getRefunds(filter, page, sort);

        assertNull(actualResponse.getContinuationId());
        assertTrue(actualResponse.getData().getRefunds().isEmpty());
    }

    @Test
    void getRefundsWithoutLastId() {
        Filter filter = new Filter();
        Page page = new Page();
        Sort sort = new Sort();
        sort.setOrder(SortOrder.DESC);
        Refund refund = TestObjectsFactory.testRefund();
        HistoricalRefundsDto dto = HistoricalRefundsDto.builder()
                .refunds(List.of(refund))
                .lastId(null)
                .build();
        when(service.getRefunds(any(FilterDto.class))).thenReturn(dto);

        HistoricalDataResponse actualResponse = handler.getRefunds(filter, page, sort);

        assertNull(actualResponse.getContinuationId());
        assertFalse(actualResponse.getData().getRefunds().isEmpty());
        assertIterableEquals(dto.getRefunds(), actualResponse.getData().getRefunds());
    }

    @Test
    void getRefunds() {
        Filter filter = new Filter();
        Page page = new Page();
        Sort sort = new Sort();
        sort.setOrder(SortOrder.DESC);
        String lastId = TestObjectsFactory.randomString();
        Refund refund = TestObjectsFactory.testRefund();
        List<Refund> refunds = List.of(refund);
        HistoricalRefundsDto dto = HistoricalRefundsDto.builder()
                .refunds(refunds)
                .lastId(lastId)
                .build();
        when(service.getRefunds(any(FilterDto.class))).thenReturn(dto);

        HistoricalDataResponse actualResponse = handler.getRefunds(filter, page, sort);

        assertEquals(lastId, actualResponse.getContinuationId());
        assertEquals(refunds.size(), actualResponse.getData().getRefunds().size());
    }

    @Test
    void getChargebacksWithoutChargebacks() {
        Filter filter = new Filter();
        Page page = new Page();
        Sort sort = new Sort();
        sort.setOrder(SortOrder.DESC);
        HistoricalChargebacksDto dto = HistoricalChargebacksDto.builder()
                .chargebacks(Collections.emptyList())
                .lastId(null)
                .build();
        when(service.getChargebacks(any(FilterDto.class))).thenReturn(dto);

        HistoricalDataResponse actualResponse = handler.getChargebacks(filter, page, sort);

        assertNull(actualResponse.getContinuationId());
        assertTrue(actualResponse.getData().getChargebacks().isEmpty());
    }

    @Test
    void getChargebacksWithoutLastId() {
        Filter filter = new Filter();
        Page page = new Page();
        Sort sort = new Sort();
        sort.setOrder(SortOrder.DESC);
        Chargeback chargeback = TestObjectsFactory.testChargeback();
        HistoricalChargebacksDto dto = HistoricalChargebacksDto.builder()
                .chargebacks(List.of(chargeback))
                .lastId(null)
                .build();
        when(service.getChargebacks(any(FilterDto.class))).thenReturn(dto);

        HistoricalDataResponse actualResponse = handler.getChargebacks(filter, page, sort);

        assertNull(actualResponse.getContinuationId());
        assertFalse(actualResponse.getData().getChargebacks().isEmpty());
        assertIterableEquals(dto.getChargebacks(), actualResponse.getData().getChargebacks());
    }

    @Test
    void getChargebacks() {
        Filter filter = new Filter();
        Page page = new Page();
        Sort sort = new Sort();
        sort.setOrder(SortOrder.DESC);
        String lastId = TestObjectsFactory.randomString();
        Chargeback chargeback = TestObjectsFactory.testChargeback();
        List<Chargeback> chargebacks = List.of(chargeback);
        HistoricalChargebacksDto dto = HistoricalChargebacksDto.builder()
                .chargebacks(chargebacks)
                .lastId(lastId)
                .build();
        when(service.getChargebacks(any(FilterDto.class))).thenReturn(dto);

        HistoricalDataResponse actualResponse = handler.getChargebacks(filter, page, sort);

        assertEquals(lastId, actualResponse.getContinuationId());
        assertEquals(chargebacks.size(), actualResponse.getData().getChargebacks().size());
    }

    @Test
    void getFraudResultsWithoutFraudResults() {
        Filter filter = new Filter();
        Page page = new Page();
        Sort sort = new Sort();
        sort.setOrder(SortOrder.DESC);
        HistoricalFraudResultsDto dto = HistoricalFraudResultsDto.builder()
                .fraudResults(Collections.emptyList())
                .lastId(null)
                .build();
        when(service.getFraudResults(any(FilterDto.class))).thenReturn(dto);

        HistoricalDataResponse actualResponse = handler.getFraudResults(filter, page, sort);

        assertNull(actualResponse.getContinuationId());
        assertTrue(actualResponse.getData().getFraudResults().isEmpty());
    }

    @Test
    void getFraudResultsWithoutLastId() {
        Filter filter = new Filter();
        Page page = new Page();
        Sort sort = new Sort();
        sort.setOrder(SortOrder.DESC);
        Event event = TestObjectsFactory.testEvent();
        HistoricalFraudResultsDto dto = HistoricalFraudResultsDto.builder()
                .fraudResults(List.of(event))
                .lastId(null)
                .build();
        when(service.getFraudResults(any(FilterDto.class))).thenReturn(dto);

        HistoricalDataResponse actualResponse = handler.getFraudResults(filter, page, sort);

        assertNull(actualResponse.getContinuationId());
        assertFalse(actualResponse.getData().getFraudResults().isEmpty());
        HistoricalTransactionCheck actualFraudResult = actualResponse.getData().getFraudResults().get(0);
        assertEquals("bank_card",
                TBaseUtil.unionFieldToEnum(actualFraudResult.getTransaction().getPaymentTool(), PaymentToolType.class)
                        .name());
        assertEquals(event.getCardToken(),
                actualFraudResult.getTransaction().getPaymentTool().getBankCard().getToken());
        assertEquals(event.getBankName(),
                actualFraudResult.getTransaction().getPaymentTool().getBankCard().getBankName());
        assertEquals(event.getBin(), actualFraudResult.getTransaction().getPaymentTool().getBankCard().getBin());
        assertEquals(event.getMaskedPan(),
                actualFraudResult.getTransaction().getPaymentTool().getBankCard().getLastDigits());
        assertEquals(event.getIp(), actualFraudResult.getTransaction().getClientInfo().getIp());
        assertEquals(event.getEmail(), actualFraudResult.getTransaction().getClientInfo().getEmail());
        assertEquals(event.getFingerprint(), actualFraudResult.getTransaction().getClientInfo().getFingerprint());
        assertEquals(event.getPartyId(),
                actualFraudResult.getTransaction().getReferenceInfo().getMerchantInfo().getPartyId());
        assertEquals(event.getShopId(),
                actualFraudResult.getTransaction().getReferenceInfo().getMerchantInfo().getShopId());
        assertEquals(event.getCheckedRule(),
                actualFraudResult.getCheckResult().getConcreteCheckResult().getRuleChecked());
        assertEquals(event.getCheckedTemplate(), actualFraudResult.getCheckResult().getCheckedTemplate());
        assertEquals(ResultStatus.accept(new Accept()),
                actualFraudResult.getCheckResult().getConcreteCheckResult().getResultStatus());
        assertEquals(event.getAmount(), actualFraudResult.getTransaction().getCost().getAmount());
        assertEquals(event.getCurrency(), actualFraudResult.getTransaction().getCost().getCurrency().getSymbolicCode());
        assertEquals(LocalDateTime.ofInstant(
                Instant.ofEpochSecond(event.getEventTime()), ZoneId.of("UTC"))
                        .format(DateTimeFormatter.ISO_DATE_TIME),
                actualFraudResult.getTransaction().getEventTime());
        assertEquals(event.getPaymentId(), actualFraudResult.getTransaction().getId());
    }

    @Test
    void getFraudResults() {
        Filter filter = new Filter();
        Page page = new Page();
        Sort sort = new Sort();
        sort.setOrder(SortOrder.DESC);
        String lastId = TestObjectsFactory.randomString();
        Event event = TestObjectsFactory.testEvent();
        List<Event> events = List.of(event);
        HistoricalFraudResultsDto dto = HistoricalFraudResultsDto.builder()
                .fraudResults(events)
                .lastId(lastId)
                .build();
        when(service.getFraudResults(any(FilterDto.class))).thenReturn(dto);

        HistoricalDataResponse actualResponse = handler.getFraudResults(filter, page, sort);

        assertEquals(lastId, actualResponse.getContinuationId());
        assertEquals(events.size(), actualResponse.getData().getFraudResults().size());
    }

    @Test
    void getFraudPaymentsWithoutFraudPayments() {
        Filter filter = new Filter();
        Page page = new Page();
        Sort sort = new Sort();
        sort.setOrder(SortOrder.DESC);
        HistoricalPaymentsDto dto = HistoricalPaymentsDto.builder()
                .payments(Collections.emptyList())
                .lastId(null)
                .build();
        when(service.getFraudPayments(any(FilterDto.class))).thenReturn(dto);

        HistoricalDataResponse actualResponse = handler.getFraudPayments(filter, page, sort);

        assertNull(actualResponse.getContinuationId());
        assertTrue(actualResponse.getData().getFraudPayments().isEmpty());
    }

    @Test
    void getFraudPaymentsWithoutLastId() {
        Filter filter = new Filter();
        Page page = new Page();
        Sort sort = new Sort();
        sort.setOrder(SortOrder.DESC);
        FraudPaymentRow fraudPaymentRow = TestObjectsFactory.testFraudPaymentRow();
        HistoricalPaymentsDto dto = HistoricalPaymentsDto.builder()
                .payments(List.of(fraudPaymentRow))
                .lastId(null)
                .build();
        when(service.getFraudPayments(any(FilterDto.class))).thenReturn(dto);

        HistoricalDataResponse actualResponse = handler.getFraudPayments(filter, page, sort);

        assertNull(actualResponse.getContinuationId());
        assertFalse(actualResponse.getData().getFraudPayments().isEmpty());
        FraudPaymentInfo actualFraudPaymentInfo = actualResponse.getData().getFraudPayments().get(0);
        assertEquals("bank_card",
                TBaseUtil.unionFieldToEnum(actualFraudPaymentInfo.getPayment().getPaymentTool(), PaymentToolType.class)
                        .name());
        assertEquals(fraudPaymentRow.getPaymentSystem(),
                actualFraudPaymentInfo.getPayment().getPaymentTool().getBankCard().getPaymentSystem().getId());
        assertEquals(fraudPaymentRow.getCardToken(),
                actualFraudPaymentInfo.getPayment().getPaymentTool().getBankCard().getToken());
        assertEquals(fraudPaymentRow.getIp(), actualFraudPaymentInfo.getPayment().getClientInfo().getIp());
        assertEquals(fraudPaymentRow.getEmail(), actualFraudPaymentInfo.getPayment().getClientInfo().getEmail());
        assertEquals(fraudPaymentRow.getFingerprint(),
                actualFraudPaymentInfo.getPayment().getClientInfo().getFingerprint());
        assertEquals(fraudPaymentRow.getPartyId(),
                actualFraudPaymentInfo.getPayment().getReferenceInfo().getMerchantInfo().getPartyId());
        assertEquals(fraudPaymentRow.getShopId(),
                actualFraudPaymentInfo.getPayment().getReferenceInfo().getMerchantInfo().getShopId());
        assertEquals(fraudPaymentRow.getProviderId(),
                actualFraudPaymentInfo.getPayment().getProviderInfo().getProviderId());
        assertEquals(fraudPaymentRow.getTerminal(),
                actualFraudPaymentInfo.getPayment().getProviderInfo().getTerminalId());
        assertEquals(fraudPaymentRow.getBankCountry(),
                actualFraudPaymentInfo.getPayment().getProviderInfo().getCountry());
        assertEquals(fraudPaymentRow.getPaymentStatus(), actualFraudPaymentInfo.getPayment().getStatus().toString());
        assertEquals(fraudPaymentRow.getAmount(), actualFraudPaymentInfo.getPayment().getCost().getAmount());
        assertEquals(LocalDateTime.ofInstant(
                Instant.ofEpochSecond(fraudPaymentRow.getEventTime()), ZoneId.of("UTC"))
                        .format(DateTimeFormatter.ISO_DATE_TIME),
                actualFraudPaymentInfo.getPayment().getEventTime());
        assertEquals(fraudPaymentRow.getId(), actualFraudPaymentInfo.getPayment().getId());
        assertEquals(fraudPaymentRow.getComment(), actualFraudPaymentInfo.getComment());
        assertEquals(fraudPaymentRow.getType(), actualFraudPaymentInfo.getType());
    }

    @Test
    void getFraudPayments() {
        Filter filter = new Filter();
        Page page = new Page();
        Sort sort = new Sort();
        sort.setOrder(SortOrder.DESC);
        String lastId = TestObjectsFactory.randomString();
        FraudPaymentRow fraudPaymentRow = TestObjectsFactory.testFraudPaymentRow();
        List<FraudPaymentRow> fraudPaymentRows = List.of(fraudPaymentRow);
        HistoricalPaymentsDto dto = HistoricalPaymentsDto.builder()
                .payments(fraudPaymentRows)
                .lastId(lastId)
                .build();
        when(service.getFraudPayments(any(FilterDto.class))).thenReturn(dto);

        HistoricalDataResponse actualResponse = handler.getFraudPayments(filter, page, sort);

        assertEquals(lastId, actualResponse.getContinuationId());
        assertEquals(fraudPaymentRows.size(), actualResponse.getData().getFraudPayments().size());
    }
}