package com.rbkmoney.fraudbusters.resource;

import com.rbkmoney.damsel.fraudbusters.*;
import com.rbkmoney.fraudbusters.TestObjectsFactory;
import com.rbkmoney.fraudbusters.converter.CheckedPaymentToPaymentInfoConverter;
import com.rbkmoney.fraudbusters.converter.FilterConverter;
import com.rbkmoney.fraudbusters.converter.PaymentInfoResultConverter;
import com.rbkmoney.fraudbusters.domain.CheckedPayment;
import com.rbkmoney.fraudbusters.exception.InvalidTemplateException;
import com.rbkmoney.fraudbusters.service.HistoricalDataService;
import com.rbkmoney.fraudbusters.service.dto.FilterDto;
import com.rbkmoney.fraudbusters.service.dto.HistoricalPaymentsDto;
import org.apache.thrift.TException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {HistoricalDataHandler.class, CheckedPaymentToPaymentInfoConverter.class,
        FilterConverter.class, PaymentInfoResultConverter.class})
class HistoricalDataHandlerTest {

    @Autowired
    private HistoricalDataHandler handler;

    @MockBean
    private HistoricalDataService service;

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

    @Test
    void applyRuleOnHistoricalDataSetThrowsHistoricalDataServiceException() {
        EmulationRuleApplyRequest request = createEmulationRuleApplyRequest();
        when(service.applySingleRule(
                request.getEmulationRule().getTemplateEmulation().getTemplate(),
                request.getTransactions()
        ))
                .thenThrow(new InvalidTemplateException());

        assertThrows(HistoricalDataServiceException.class, () -> handler.applyRuleOnHistoricalDataSet(request));
    }

    @Test
    void applyRuleOnHistoricalDataSetApplySingleRule() throws TException {
        Set<HistoricalTransactionCheck> expectedSet = Set.of(
                new HistoricalTransactionCheck().setTransaction(createPaymentInfo()),
                new HistoricalTransactionCheck().setTransaction(createPaymentInfo())
        );
        EmulationRuleApplyRequest request = createEmulationRuleApplyRequest();
        when(service.applySingleRule(
                request.getEmulationRule().getTemplateEmulation().getTemplate(),
                request.getTransactions()
        ))
                .thenReturn(expectedSet);

        HistoricalDataSetCheckResult actual = handler.applyRuleOnHistoricalDataSet(request);
        assertEquals(expectedSet, actual.getHistoricalTransactionCheck());
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
        request.setTransactions(Set.of(createPaymentInfo(), createPaymentInfo()));

        HistoricalDataSetCheckResult actual = handler.applyRuleOnHistoricalDataSet(request);

        verify(service, times(0)).applySingleRule(any(), any());
        assertNull(actual.getHistoricalTransactionCheck());
    }

    private EmulationRuleApplyRequest createEmulationRuleApplyRequest() {
        Template template = new Template();
        template.setId(TEMPLATE_ID);
        template.setTemplate(TEMPLATE.getBytes(StandardCharsets.UTF_8));
        OnlyTemplateEmulation onlyTemplateEmulation = new OnlyTemplateEmulation();
        onlyTemplateEmulation.setTemplate(template);
        EmulationRule emulationRule = new EmulationRule();
        emulationRule.setTemplateEmulation(onlyTemplateEmulation);
        EmulationRuleApplyRequest request = new EmulationRuleApplyRequest();
        request.setEmulationRule(emulationRule);
        request.setTransactions(Set.of(createPaymentInfo(), createPaymentInfo()));

        return request;
    }

    private PaymentInfo createPaymentInfo() {
        PaymentInfo paymentInfo = new PaymentInfo();
        paymentInfo.setId(UUID.randomUUID().toString());

        return paymentInfo;
    }

}
