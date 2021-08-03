package com.rbkmoney.fraudbusters.resource;

import com.rbkmoney.damsel.domain.Cash;
import com.rbkmoney.damsel.domain.CurrencyRef;
import com.rbkmoney.damsel.fraudbusters.*;
import com.rbkmoney.fraudbusters.converter.FilterConverter;
import com.rbkmoney.fraudbusters.converter.PaymentToPaymentModelConverter;
import com.rbkmoney.fraudbusters.exception.InvalidTemplateException;
import com.rbkmoney.fraudbusters.fraud.model.PaymentModel;
import com.rbkmoney.fraudbusters.service.HistoricalDataService;
import com.rbkmoney.fraudbusters.service.RuleTestingService;
import com.rbkmoney.fraudbusters.util.HistoricalTransactionCheckFactory;
import com.rbkmoney.fraudo.constant.ResultStatus;
import com.rbkmoney.fraudo.model.ResultModel;
import com.rbkmoney.fraudo.model.RuleResult;
import org.apache.thrift.TException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
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
@SpringBootTest(classes = {HistoricalDataHandler.class, FilterConverter.class})
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

    /*
    TODO: uncomment when other methods are going to be implemented
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

        PaymentInfoResult actualPayments = handler.getPayments(filter, page, sort);

        assertNull(actualPayments.getContinuationId());
        assertTrue(actualPayments.getPayments().isEmpty());
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

        PaymentInfoResult actualPayments = handler.getPayments(filter, page, sort);

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

        PaymentInfoResult actualPayments = handler.getPayments(filter, page, sort);

        assertEquals(lastId, actualPayments.getContinuationId());
        assertEquals(checkedPayments.size(), actualPayments.getPaymentsSize());

    }
    */

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
        Payment firstPayment = createPayment(firstAmount);
        Payment secondPayment = createPayment(secondAmount);
        PaymentModel firstPaymentModel = createPaymentModel(firstAmount);
        PaymentModel secondPaymentModel = createPaymentModel(secondAmount);
        var acceptedStatus = new com.rbkmoney.damsel.fraudbusters.ResultStatus();
        acceptedStatus.setAccept(new Accept());
        var declinedStatus = new com.rbkmoney.damsel.fraudbusters.ResultStatus();
        declinedStatus.setAccept(new Accept());
        HistoricalTransactionCheck acceptedCheck = createHistoricalTransactionCheck(firstPayment, acceptedStatus);
        HistoricalTransactionCheck declinedCheck = createHistoricalTransactionCheck(secondPayment, declinedStatus);
        ResultModel firstResultModel = createResultModel(ResultStatus.ACCEPT);
        ResultModel secondResultModel = createResultModel(ResultStatus.DECLINE);
        EmulationRuleApplyRequest request = createEmulationRuleApplyRequest(Set.of(firstPayment, secondPayment));

        when(paymentModelConverter.convert(any(Payment.class)))
                .thenReturn(firstPaymentModel)
                .thenReturn(secondPaymentModel);
        when(ruleTestingService.applySingleRule(anyMap(), anyString()))
                .thenReturn(Map.of(
                        firstPayment.getId(), firstResultModel,
                        secondPayment.getId(), secondResultModel
                ));
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

    private ResultModel createResultModel(ResultStatus resultStatus) {
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
