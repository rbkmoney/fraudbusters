package com.rbkmoney.fraudbusters.resource.payment.handler;

import com.rbkmoney.damsel.domain.Cash;
import com.rbkmoney.damsel.domain.CurrencyRef;
import com.rbkmoney.damsel.fraudbusters.*;
import com.rbkmoney.fraudbusters.TestObjectsFactory;
import com.rbkmoney.fraudbusters.constant.PaymentToolType;
import com.rbkmoney.fraudbusters.converter.*;
import com.rbkmoney.fraudbusters.domain.CheckedPayment;
import com.rbkmoney.fraudbusters.domain.Event;
import com.rbkmoney.fraudbusters.domain.FraudPaymentRow;
import com.rbkmoney.fraudbusters.exception.InvalidTemplateException;
import com.rbkmoney.fraudbusters.fraud.model.PaymentModel;
import com.rbkmoney.fraudbusters.service.HistoricalDataService;
import com.rbkmoney.fraudbusters.service.RuleTestingService;
import com.rbkmoney.fraudbusters.service.dto.*;
import com.rbkmoney.fraudbusters.util.HistoricalTransactionCheckFactory;
import com.rbkmoney.fraudo.model.ResultModel;
import com.rbkmoney.fraudo.model.RuleResult;
import com.rbkmoney.geck.common.util.TBaseUtil;
import org.apache.thrift.TException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
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

    @MockBean
    private RuleTestingService ruleTestingService;

    @MockBean
    private PaymentToPaymentModelConverter paymentModelConverter;

    @MockBean
    private HistoricalTransactionCheckFactory historicalTransactionCheckFactory;

    private static final String TEMPLATE = UUID.randomUUID().toString();
    private static final String TEMPLATE_ID = UUID.randomUUID().toString();


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
        assertEquals(Collections.emptyList(),
                actualFraudResult.getCheckResult().getConcreteCheckResult().getNotificationsRule());
        assertEquals(event.getAmount(), actualFraudResult.getTransaction().getCost().getAmount());
        assertEquals(event.getCurrency(), actualFraudResult.getTransaction().getCost().getCurrency().getSymbolicCode());
        assertEquals(LocalDateTime.ofInstant(
                Instant.ofEpochSecond(event.getEventTime()), ZoneId.of("UTC"))
                        .format(DateTimeFormatter.ISO_DATE_TIME),
                actualFraudResult.getTransaction().getEventTime());
        assertEquals(event.getPaymentId(), actualFraudResult.getTransaction().getId());
        assertEquals(PaymentStatus.unknown, actualFraudResult.getTransaction().getStatus());
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

    @Test
    void applyRuleOnHistoricalDataSetThrowsHistoricalDataServiceException() {
        EmulationRuleApplyRequest request = createEmulationRuleApplyRequest();
        when(paymentModelConverter.convert(any(Payment.class)))
                .thenReturn(createPaymentModel(ThreadLocalRandom.current().nextLong()));
        when(ruleTestingService.applySingleRule(any(), anyString())).thenThrow(new InvalidTemplateException());
        assertThrows(HistoricalDataServiceException.class, () -> handler.applyRuleOnHistoricalDataSet(request));
    }

    @Test
    void applyRuleOnHistoricalDataSetApplySingleRule() throws TException {
        long firstAmount = 1L;
        long secondAmount = 2L;
        var acceptedStatus = new ResultStatus();
        acceptedStatus.setAccept(new Accept());
        var declinedStatus = new ResultStatus();
        declinedStatus.setAccept(new Accept());
        Payment firstPayment = createPayment(firstAmount);
        Payment secondPayment = createPayment(secondAmount);
        ResultModel firstResultModel = createResultModel(com.rbkmoney.fraudo.constant.ResultStatus.ACCEPT);
        ResultModel secondResultModel = createResultModel(com.rbkmoney.fraudo.constant.ResultStatus.DECLINE);
        Map<String, ResultModel> resultModelMap = new LinkedHashMap<>();
        resultModelMap.put(firstPayment.getId(), firstResultModel);
        resultModelMap.put(secondPayment.getId(), secondResultModel);
        EmulationRuleApplyRequest request = createEmulationRuleApplyRequest(firstPayment, secondPayment);
        HistoricalTransactionCheck acceptedCheck = createHistoricalTransactionCheck(firstPayment, acceptedStatus);
        HistoricalTransactionCheck declinedCheck = createHistoricalTransactionCheck(secondPayment, declinedStatus);
        PaymentModel firstPaymentModel = createPaymentModel(firstAmount);
        PaymentModel secondPaymentModel = createPaymentModel(secondAmount);

        when(paymentModelConverter.convert(any(Payment.class)))
                .thenReturn(firstPaymentModel)
                .thenReturn(secondPaymentModel);
        when(ruleTestingService.applySingleRule(anyMap(), anyString())).thenReturn(resultModelMap);
        when(historicalTransactionCheckFactory
                .createHistoricalTransactionCheck(any(Payment.class), anyString(), any(ResultModel.class))
        )
                .thenReturn(acceptedCheck)
                .thenReturn(declinedCheck);

        HistoricalDataSetCheckResult actual = handler.applyRuleOnHistoricalDataSet(request);

        //result verification
        assertEquals(Set.of(acceptedCheck, declinedCheck), actual.getHistoricalTransactionCheck());

        // verify mocks
        // payment model convertor verification
        ArgumentCaptor<Payment> paymentModelConverterPaymentCaptor = ArgumentCaptor.forClass(Payment.class);
        verify(paymentModelConverter, times(2))
                .convert(paymentModelConverterPaymentCaptor.capture());
        assertEquals(2, paymentModelConverterPaymentCaptor.getAllValues().size());
        assertEquals(List.of(firstPayment, secondPayment), paymentModelConverterPaymentCaptor.getAllValues());

        // rule testing service mock verification
        ArgumentCaptor<Map<String, PaymentModel>> mapCaptor = ArgumentCaptor.forClass(Map.class);
        ArgumentCaptor<String> serviceTemplateStringCaptor = ArgumentCaptor.forClass(String.class);
        verify(ruleTestingService, times(1))
                .applySingleRule(mapCaptor.capture(), serviceTemplateStringCaptor.capture());
        assertEquals(1, mapCaptor.getAllValues().size());
        Map<String, PaymentModel> paymentModelMap = mapCaptor.getAllValues().get(0);
        assertEquals(2, paymentModelMap.size());
        assertEquals(firstPaymentModel, paymentModelMap.get(firstPayment.getId()));
        assertEquals(secondPaymentModel, paymentModelMap.get(secondPayment.getId()));
        assertEquals(1, serviceTemplateStringCaptor.getAllValues().size());
        assertEquals(TEMPLATE, serviceTemplateStringCaptor.getAllValues().get(0));

        // historical transaction check factory mock verification
        ArgumentCaptor<Payment> checkResultFactoryPaymentCaptor = ArgumentCaptor.forClass(Payment.class);
        ArgumentCaptor<String> checkResultFactoryTemplateStringCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<ResultModel> resultModelCaptor = ArgumentCaptor.forClass(ResultModel.class);
        verify(historicalTransactionCheckFactory, times(2)).createHistoricalTransactionCheck(
                checkResultFactoryPaymentCaptor.capture(),
                checkResultFactoryTemplateStringCaptor.capture(),
                resultModelCaptor.capture()
        );
        assertEquals(2, checkResultFactoryPaymentCaptor.getAllValues().size());
        assertEquals(List.of(firstPayment, secondPayment), checkResultFactoryPaymentCaptor.getAllValues());
        assertEquals(2, checkResultFactoryTemplateStringCaptor.getAllValues().size());
        assertEquals(List.of(TEMPLATE, TEMPLATE), checkResultFactoryTemplateStringCaptor.getAllValues());
        assertEquals(2, resultModelCaptor.getAllValues().size());
        assertEquals(List.of(firstResultModel, secondResultModel), resultModelCaptor.getAllValues());
    }

    @Test
    void applyRuleOnHistoricalDataSetApplyRuleWithRuleSet() throws TException {
        Template template = new Template();
        template.setId(TEMPLATE_ID);
        template.setTemplate(TEMPLATE.getBytes(StandardCharsets.UTF_8));
        TemplateReference templateReference = new TemplateReference();
        templateReference.setTemplateId(TEMPLATE_ID);
        templateReference.setPartyId(UUID.randomUUID().toString());
        templateReference.setShopId(UUID.randomUUID().toString());
        CascasdingTemplateEmulation cascasdingTemplateEmulation = new CascasdingTemplateEmulation();
        cascasdingTemplateEmulation.setTemplate(template);
        cascasdingTemplateEmulation.setRef(templateReference);
        EmulationRule emulationRule = new EmulationRule();
        emulationRule.setCascadingEmulation(cascasdingTemplateEmulation);
        EmulationRuleApplyRequest request = new EmulationRuleApplyRequest();
        request.setEmulationRule(emulationRule);
        request.setTransactions(Set.of(createPayment(), createPayment()));

        HistoricalDataSetCheckResult actual = handler.applyRuleOnHistoricalDataSet(request);

        verify(ruleTestingService, times(0)).applySingleRule(any(), any());
        assertNull(actual.getHistoricalTransactionCheck());
    }

    private EmulationRuleApplyRequest createEmulationRuleApplyRequest() {
        return createEmulationRuleApplyRequest(Set.of(createPayment(), createPayment()));
    }

    private EmulationRuleApplyRequest createEmulationRuleApplyRequest(Payment... payments) {
        Set<Payment> transactions = new LinkedHashSet<>();
        for (Payment payment : payments) {
            transactions.add(payment);
        }
        return createEmulationRuleApplyRequest(transactions);
    }

    private EmulationRuleApplyRequest createEmulationRuleApplyRequest(Set<Payment> transactions) {
        Template template = new Template();
        template.setId(TEMPLATE_ID);
        template.setTemplate(TEMPLATE.getBytes(StandardCharsets.UTF_8));
        OnlyTemplateEmulation onlyTemplateEmulation = new OnlyTemplateEmulation();
        onlyTemplateEmulation.setTemplate(template);
        EmulationRule emulationRule = new EmulationRule();
        emulationRule.setTemplateEmulation(onlyTemplateEmulation);
        EmulationRuleApplyRequest request = new EmulationRuleApplyRequest();
        request.setEmulationRule(emulationRule);
        request.setTransactions(transactions);

        return request;
    }

    private Payment createPayment() {
        return createPayment(ThreadLocalRandom.current().nextLong());
    }

    private Payment createPayment(Long amount) {
        return new Payment()
                .setId(UUID.randomUUID().toString())
                .setCost(new Cash()
                        .setAmount(amount)
                        .setCurrency(new CurrencyRef("RUB"))
                );
    }

    private PaymentModel createPaymentModel(Long amount) {
        PaymentModel paymentModel = new PaymentModel();
        paymentModel.setAmount(amount);

        return paymentModel;
    }

    private ResultModel createResultModel(com.rbkmoney.fraudo.constant.ResultStatus resultStatus) {
        RuleResult ruleResult = new RuleResult();
        ruleResult.setRuleChecked(TEMPLATE);
        ruleResult.setResultStatus(resultStatus);
        ResultModel resultModel = new ResultModel();
        resultModel.setRuleResults(Collections.singletonList(ruleResult));

        return resultModel;
    }

    private HistoricalTransactionCheck createHistoricalTransactionCheck(
            Payment payment, com.rbkmoney.damsel.fraudbusters.ResultStatus resultStatus
    ) {
        return new HistoricalTransactionCheck()
                .setTransaction(payment)
                .setCheckResult(new CheckResult()
                        .setCheckedTemplate(TEMPLATE)
                        .setConcreteCheckResult(new ConcreteCheckResult()
                                .setRuleChecked("0")
                                .setResultStatus(resultStatus)
                        )
                );
    }
}
