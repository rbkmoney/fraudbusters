package com.rbkmoney.fraudbusters.service;

import com.rbkmoney.fraudbusters.exception.InvalidTemplateException;
import com.rbkmoney.fraudbusters.fraud.FraudContextParser;
import com.rbkmoney.fraudbusters.fraud.model.PaymentModel;
import com.rbkmoney.fraudbusters.fraud.payment.validator.PaymentTemplateValidator;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {RuleCheckingServiceImpl.class})
class RuleCheckingServiceImplTest {

    @Autowired
    private RuleCheckingServiceImpl ruleTestingService;

    @MockBean
    private FraudContextParser<FraudoPaymentParser.ParseContext> paymentContextParser;

    @MockBean
    private PaymentTemplateValidator paymentTemplateValidator;

    @MockBean
    private TemplateVisitor<PaymentModel, ResultModel> paymentRuleVisitor;

    private static final String TEMPLATE = "rule: amount > 10 -> accept;";

    @Test
    void applySingleRuleThrowsInvalidTemplateException() {
        when(paymentTemplateValidator.validate(TEMPLATE)).thenReturn(List.of("123", "321"));
        assertThrows(InvalidTemplateException.class, () -> ruleTestingService.checkSingleRule(
                Map.of(
                        UUID.randomUUID().toString(), createPaymentModel(0L),
                        UUID.randomUUID().toString(), createPaymentModel(1L)),
                TEMPLATE
        ));
    }

    @Test
    void applySingleRule() {
        FraudoPaymentParser.ParseContext context = new FraudoPaymentParser.ParseContext(null, 0);
        ResultModel firstResultModel = createResultModel(ResultStatus.ACCEPT);
        ResultModel secondResultModel = createResultModel(ResultStatus.DECLINE);
        when(paymentTemplateValidator.validate(anyString())).thenReturn(new ArrayList<>());
        when(paymentContextParser.parse(anyString())).thenReturn(context);
        when(paymentRuleVisitor.visit(any(), any()))
                .thenReturn(firstResultModel)
                .thenReturn(secondResultModel);

        String firstId = UUID.randomUUID().toString();
        String secondId = UUID.randomUUID().toString();
        PaymentModel firstPaymentModel = createPaymentModel(25L);
        PaymentModel secondPaymentModel = createPaymentModel(2L);
        Map<String, PaymentModel> paymentModelMap = new LinkedHashMap<>();
        paymentModelMap.put(firstId, firstPaymentModel);
        paymentModelMap.put(secondId, secondPaymentModel);

        Map<String, ResultModel> actual = ruleTestingService.checkSingleRule(paymentModelMap, TEMPLATE);

        //result verification
        assertEquals(2, actual.size());
        assertEquals(firstResultModel, actual.get(firstId));
        assertEquals(secondResultModel, actual.get(secondId));

        //mock cheсks
        ArgumentCaptor<String> validatorTemplateStringCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> contextParserTemplateStringCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<FraudoPaymentParser.ParseContext> contextCaptor =
                ArgumentCaptor.forClass(FraudoPaymentParser.ParseContext.class);
        ArgumentCaptor<PaymentModel> paymentModelCaptor = ArgumentCaptor.forClass(PaymentModel.class);

        verify(paymentTemplateValidator, times(1))
                .validate(validatorTemplateStringCaptor.capture());
        verify(paymentContextParser, times(1))
                .parse(contextParserTemplateStringCaptor.capture());
        verify(paymentRuleVisitor, times(2))
                .visit(contextCaptor.capture(), paymentModelCaptor.capture());

        //template validator verification
        assertEquals(1, validatorTemplateStringCaptor.getAllValues().size());
        assertEquals(TEMPLATE, validatorTemplateStringCaptor.getValue());

        //context parser verification
        assertEquals(1, contextParserTemplateStringCaptor.getAllValues().size());
        assertEquals(TEMPLATE, contextParserTemplateStringCaptor.getValue());

        //template visitor verification
        assertEquals(2, contextCaptor.getAllValues().size());
        assertEquals(context, contextCaptor.getAllValues().get(0));
        assertEquals(context, contextCaptor.getAllValues().get(1));
        assertEquals(2, paymentModelCaptor.getAllValues().size());
        assertEquals(firstPaymentModel, paymentModelCaptor.getAllValues().get(0));
        assertEquals(secondPaymentModel, paymentModelCaptor.getAllValues().get(1));
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
}
