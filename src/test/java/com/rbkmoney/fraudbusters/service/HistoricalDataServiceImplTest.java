package com.rbkmoney.fraudbusters.service;

import com.rbkmoney.damsel.domain.Cash;
import com.rbkmoney.damsel.fraudbusters.Accept;
import com.rbkmoney.damsel.fraudbusters.CheckResult;
import com.rbkmoney.damsel.fraudbusters.ConcreteCheckResult;
import com.rbkmoney.damsel.fraudbusters.Decline;
import com.rbkmoney.damsel.fraudbusters.HistoricalTransactionCheck;
import com.rbkmoney.damsel.fraudbusters.Payment;
import com.rbkmoney.damsel.fraudbusters.Template;
import com.rbkmoney.fraudbusters.TestObjectsFactory;
import com.rbkmoney.fraudbusters.converter.PaymentToPaymentModelConverter;
import com.rbkmoney.fraudbusters.domain.CheckedPayment;
import com.rbkmoney.fraudbusters.exception.InvalidTemplateException;
import com.rbkmoney.fraudbusters.fraud.FraudContextParser;
import com.rbkmoney.fraudbusters.fraud.model.PaymentModel;
import com.rbkmoney.fraudbusters.fraud.payment.validator.PaymentTemplateValidator;
import com.rbkmoney.fraudbusters.repository.Repository;
import com.rbkmoney.fraudbusters.service.dto.FilterDto;
import com.rbkmoney.fraudbusters.service.dto.HistoricalPaymentsDto;
import com.rbkmoney.fraudbusters.util.CheckResultFactory;
import com.rbkmoney.fraudo.FraudoPaymentParser;
import com.rbkmoney.fraudo.constant.ResultStatus;
import com.rbkmoney.fraudo.model.ResultModel;
import com.rbkmoney.fraudo.model.RuleResult;
import com.rbkmoney.fraudo.visitor.TemplateVisitor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {HistoricalDataServiceImpl.class})
class HistoricalDataServiceImplTest {

    @Autowired
    private HistoricalDataService historicalDataService;

    @MockBean
    private Repository<CheckedPayment> paymentRepository;

    @MockBean
    private FraudContextParser<FraudoPaymentParser.ParseContext> paymentContextParser;

    @MockBean
    private PaymentTemplateValidator paymentTemplateValidator;

    @MockBean
    private TemplateVisitor<PaymentModel, ResultModel> paymentRuleVisitor;

    @MockBean
    private PaymentToPaymentModelConverter paymentModelConverter;

    @MockBean
    private CheckResultFactory checkResultFactory;

    private static final String TEMPLATE_ID = UUID.randomUUID().toString();
    private static final String TEMPLATE = "rule: amount > 10 -> accept;";


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
    void applySingleRuleThrowsInvalidTemplateException() {
        Template template = createTemplate();
        when(paymentTemplateValidator.validate(TEMPLATE)).thenReturn(List.of("123", "321"));

        assertThrows(InvalidTemplateException.class, () -> historicalDataService.applySingleRule(
                template,
                Set.of(createPayment(0L), createPayment(1L))
        ));
    }

    @Test
    void applySingleRule() {
        FraudoPaymentParser.ParseContext context = new FraudoPaymentParser.ParseContext(null, 0);
        CheckResult acceptCheckResult =
                createCheckResult(com.rbkmoney.damsel.fraudbusters.ResultStatus.accept(new Accept()));
        CheckResult declineCheckResult =
                createCheckResult(com.rbkmoney.damsel.fraudbusters.ResultStatus.decline(new Decline()));
        when(paymentTemplateValidator.validate(anyString())).thenReturn(new ArrayList<>());
        when(paymentContextParser.parse(anyString())).thenReturn(context);
        when(paymentModelConverter.convert(any(Payment.class)))
                .thenReturn(createPaymentModel(25L))
                .thenReturn(createPaymentModel(2L));
        when(paymentRuleVisitor.visit(any(), any()))
                .thenReturn(createResultModel(ResultStatus.ACCEPT))
                .thenReturn(createResultModel(ResultStatus.DECLINE));
        when(checkResultFactory.createCheckResult(anyString(), any(ResultModel.class)))
                .thenReturn(acceptCheckResult)
                .thenReturn(declineCheckResult);
        Payment successTransaction = createPayment(25L);
        Payment failedTransaction = createPayment(2L);
        Set<Payment> transactions = new LinkedHashSet<>();
        transactions.add(successTransaction);
        transactions.add(failedTransaction);
        Template template = createTemplate();

        Set<HistoricalTransactionCheck> actual = historicalDataService.applySingleRule(template, transactions);

        //result verification
        assertEquals(2, actual.size());
        HistoricalTransactionCheck acceptedTransactionCheck =
                findHistoricalTransactionCheckByCheckResult(actual, acceptCheckResult);
        assertEquals(successTransaction, acceptedTransactionCheck.getTransaction());
        HistoricalTransactionCheck declinedTransactionCheck =
                findHistoricalTransactionCheckByCheckResult(actual, declineCheckResult);
        assertEquals(failedTransaction, declinedTransactionCheck.getTransaction());

        //mock cheсks

        ArgumentCaptor<String> validatorTemplateStringCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> contextParserTemplateStringCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Payment> paymentCaptor = ArgumentCaptor.forClass(Payment.class);
        ArgumentCaptor<FraudoPaymentParser.ParseContext> contextCaptor =
                ArgumentCaptor.forClass(FraudoPaymentParser.ParseContext.class);
        ArgumentCaptor<PaymentModel> paymentModelCaptor = ArgumentCaptor.forClass(PaymentModel.class);
        ArgumentCaptor<String> checkResultFactoryTemplateStringCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<ResultModel> resultModelCaptor = ArgumentCaptor.forClass(ResultModel.class);

        verify(paymentTemplateValidator, times(1))
                .validate(validatorTemplateStringCaptor.capture());
        verify(paymentContextParser, times(1))
                .parse(contextParserTemplateStringCaptor.capture());
        verify(paymentModelConverter, times(2))
                .convert(paymentCaptor.capture());
        verify(paymentRuleVisitor, times(2))
                .visit(contextCaptor.capture(), paymentModelCaptor.capture());
        verify(checkResultFactory, times(2))
                .createCheckResult(checkResultFactoryTemplateStringCaptor.capture(), resultModelCaptor.capture());

        final String expectedTemplateString = new String(template.getTemplate(), StandardCharsets.UTF_8);
        //template validator verification
        assertEquals(1, validatorTemplateStringCaptor.getAllValues().size());
        assertEquals(expectedTemplateString, validatorTemplateStringCaptor.getValue());

        //context parser verification
        assertEquals(1, contextParserTemplateStringCaptor.getAllValues().size());
        assertEquals(expectedTemplateString, contextParserTemplateStringCaptor.getValue());

        //payment info to payment model convertion verification
        assertEquals(2, paymentCaptor.getAllValues().size());
        assertEquals(successTransaction, paymentCaptor.getAllValues().get(0));
        assertEquals(failedTransaction, paymentCaptor.getAllValues().get(1));

        //template visitor verification
        assertEquals(2, contextCaptor.getAllValues().size());
        assertEquals(context, contextCaptor.getAllValues().get(0));
        assertEquals(context, contextCaptor.getAllValues().get(1));
        assertEquals(2, paymentModelCaptor.getAllValues().size());
        assertEquals(
                createPaymentModel(successTransaction.getCost().getAmount()),
                paymentModelCaptor.getAllValues().get(0)
        );
        assertEquals(
                createPaymentModel(failedTransaction.getCost().getAmount()),
                paymentModelCaptor.getAllValues().get(1)
        );

        //check result factory verification
        assertEquals(2, checkResultFactoryTemplateStringCaptor.getAllValues().size());
        assertEquals(expectedTemplateString, checkResultFactoryTemplateStringCaptor.getAllValues().get(0));
        assertEquals(expectedTemplateString, checkResultFactoryTemplateStringCaptor.getAllValues().get(1));
        assertEquals(2, resultModelCaptor.getAllValues().size());
        assertEquals(createResultModel(ResultStatus.ACCEPT), resultModelCaptor.getAllValues().get(0));
        assertEquals(createResultModel(ResultStatus.DECLINE), resultModelCaptor.getAllValues().get(1));

    }

    private Template createTemplate() {
        Template template = new Template();
        template.setId(TEMPLATE_ID);
        template.setTemplate(TEMPLATE.getBytes(StandardCharsets.UTF_8));

        return template;
    }

    private Payment createPayment(Long amount) {
        Payment payment = new Payment();
        payment.setId(UUID.randomUUID().toString());
        payment.setCost(new Cash().setAmount(amount));

        return payment;
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

    private CheckResult createCheckResult(com.rbkmoney.damsel.fraudbusters.ResultStatus resultStatus) {
        ConcreteCheckResult concreteCheckResult = new ConcreteCheckResult();
        concreteCheckResult.setResultStatus(resultStatus);
        CheckResult checkResult = new CheckResult();
        checkResult.setCheckedTemplate(TEMPLATE);
        checkResult.setConcreteCheckResult(concreteCheckResult);

        return checkResult;
    }

    private HistoricalTransactionCheck findHistoricalTransactionCheckByCheckResult(
            Set<HistoricalTransactionCheck> checks,
            CheckResult checkResult
    ) {
        return checks.stream()
                .filter(check -> check.getCheckResult().equals(checkResult))
                .findFirst()
                .orElseThrow();
    }
}
