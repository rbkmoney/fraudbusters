package com.rbkmoney.fraudbusters.stream.impl;

import com.rbkmoney.fraudbusters.domain.CheckedResultModel;
import com.rbkmoney.fraudbusters.fraud.FraudContextParser;
import com.rbkmoney.fraudbusters.fraud.model.PaymentModel;
import com.rbkmoney.fraudbusters.util.CheckedResultFactory;
import com.rbkmoney.fraudo.FraudoPaymentParser;
import com.rbkmoney.fraudo.constant.ResultStatus;
import com.rbkmoney.fraudo.model.ResultModel;
import com.rbkmoney.fraudo.model.RuleResult;
import com.rbkmoney.fraudo.visitor.TemplateVisitor;
import org.antlr.v4.runtime.ParserRuleContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.rbkmoney.fraudbusters.TestObjectsFactory.createCheckedResultModel;
import static com.rbkmoney.fraudbusters.TestObjectsFactory.createPaymentModel;
import static com.rbkmoney.fraudbusters.TestObjectsFactory.createResultModel;
import static com.rbkmoney.fraudbusters.TestObjectsFactory.createRuleResult;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RuleCheckingApplierImplTest {

    private RuleCheckingApplierImpl<PaymentModel> ruleCheckingApplier;

    @Mock
    private TemplateVisitor<PaymentModel, ResultModel> templateVisitor;

    @Mock
    private CheckedResultFactory checkedResultFactory;

    @Mock
    private FraudContextParser<FraudoPaymentParser.ParseContext> paymentContextParser;

    private static final String ACCEPT_TEMPLATE_STRING = "ACCEPT_TEMPLATE_STRING";
    private static final String DECLINE_TEMPLATE_STRING = "DECLINE_TEMPLATE_STRING";
    private static final String ACCEPT_RULE_CHECKED = "0";
    private static final String DECLINE_RULE_CHECKED = "1";
    private static final RuleResult ACCEPTED_RULE_RESULT = createRuleResult(ACCEPT_RULE_CHECKED, ResultStatus.ACCEPT);
    private static final RuleResult DECLINED_RULE_RESULT = createRuleResult(DECLINE_RULE_CHECKED, ResultStatus.DECLINE);

    @BeforeEach
    void setUp() {
        ruleCheckingApplier =
                new RuleCheckingApplierImpl<>(templateVisitor, checkedResultFactory, paymentContextParser);
    }

    @Test
    void applyNullContext() {
        when(paymentContextParser.parse(ACCEPT_TEMPLATE_STRING)).thenReturn(null);

        Optional<CheckedResultModel> actual = ruleCheckingApplier.apply(createPaymentModel(), ACCEPT_TEMPLATE_STRING);

        assertEquals(Optional.empty(), actual);
        verify(paymentContextParser, times(1)).parse(ACCEPT_TEMPLATE_STRING);
    }

    @Test
    void apply() {
        FraudoPaymentParser.ParseContext parseContext =
                new FraudoPaymentParser.ParseContext(new ParserRuleContext(), 0);
        PaymentModel paymentModel = createPaymentModel();
        ResultModel resultModel = createResultModel(List.of(ACCEPTED_RULE_RESULT, DECLINED_RULE_RESULT));
        CheckedResultModel checkedResultModel =
                createCheckedResultModel(ACCEPT_TEMPLATE_STRING, ACCEPT_RULE_CHECKED, ResultStatus.ACCEPT);

        when(paymentContextParser.parse(ACCEPT_TEMPLATE_STRING)).thenReturn(parseContext);
        when(templateVisitor.visit(parseContext, paymentModel)).thenReturn(resultModel);
        when(checkedResultFactory.createCheckedResultWithNotifications(ACCEPT_TEMPLATE_STRING, resultModel))
                .thenReturn(checkedResultModel);

        Optional<CheckedResultModel> actual = ruleCheckingApplier.apply(paymentModel, ACCEPT_TEMPLATE_STRING);
        assertEquals(Optional.of(checkedResultModel), actual);

        verify(paymentContextParser, times(1)).parse(ACCEPT_TEMPLATE_STRING);
        verify(templateVisitor, times(1)).visit(parseContext, paymentModel);
        verify(checkedResultFactory, times(1))
                .createCheckedResultWithNotifications(ACCEPT_TEMPLATE_STRING, resultModel);
    }

    @Test
    void applyForAny() {
        FraudoPaymentParser.ParseContext parseContext =
                new FraudoPaymentParser.ParseContext(new ParserRuleContext(), 0);
        PaymentModel paymentModel = createPaymentModel();
        ResultModel resultModel = createResultModel(List.of(ACCEPTED_RULE_RESULT, DECLINED_RULE_RESULT));
        CheckedResultModel checkedResultModel =
                createCheckedResultModel(ACCEPT_TEMPLATE_STRING, ACCEPT_RULE_CHECKED, ResultStatus.ACCEPT);

        when(paymentContextParser.parse(anyString())).thenReturn(parseContext);
        when(templateVisitor.visit(parseContext, paymentModel)).thenReturn(resultModel);
        when(checkedResultFactory.createCheckedResultWithNotifications(ACCEPT_TEMPLATE_STRING, resultModel))
                .thenReturn(checkedResultModel);

        Optional<CheckedResultModel> actual = ruleCheckingApplier.applyForAny(
                paymentModel,
                List.of(ACCEPT_TEMPLATE_STRING, DECLINE_TEMPLATE_STRING)
        );
        assertEquals(Optional.of(checkedResultModel), actual);

        ArgumentCaptor<String> templateStringCaptor = ArgumentCaptor.forClass(String.class);
        verify(paymentContextParser, times(1)).parse(templateStringCaptor.capture());
        assertEquals(List.of(ACCEPT_TEMPLATE_STRING), templateStringCaptor.getAllValues());
        verify(templateVisitor, times(1)).visit(parseContext, paymentModel);
        verify(checkedResultFactory, times(1))
                .createCheckedResultWithNotifications(ACCEPT_TEMPLATE_STRING, resultModel);

    }

    @Test
    void applyForAnyNullList() {
        assertEquals(Optional.empty(), ruleCheckingApplier.applyForAny(createPaymentModel(), null));
    }

    @Test
    void applyForAnyEmptyList() {
        assertEquals(Optional.empty(), ruleCheckingApplier.applyForAny(createPaymentModel(), new ArrayList<>()));
    }

    @Test
    void applyWithContextNullContext() {
        assertEquals(
                Optional.empty(),
                ruleCheckingApplier.applyWithContext(createPaymentModel(), ACCEPT_TEMPLATE_STRING, null)
        );
    }

    @Test
    void applyWithContext() {
        FraudoPaymentParser.ParseContext parseContext =
                new FraudoPaymentParser.ParseContext(new ParserRuleContext(), 0);
        PaymentModel paymentModel = createPaymentModel();
        ResultModel resultModel = createResultModel(List.of(ACCEPTED_RULE_RESULT, DECLINED_RULE_RESULT));
        CheckedResultModel checkedResultModel =
                createCheckedResultModel(ACCEPT_TEMPLATE_STRING, ACCEPT_RULE_CHECKED, ResultStatus.ACCEPT);

        when(templateVisitor.visit(parseContext, paymentModel)).thenReturn(resultModel);
        when(checkedResultFactory.createCheckedResultWithNotifications(ACCEPT_TEMPLATE_STRING, resultModel))
                .thenReturn(checkedResultModel);

        Optional<CheckedResultModel> actual =
                ruleCheckingApplier.applyWithContext(paymentModel, ACCEPT_TEMPLATE_STRING, parseContext);
        assertEquals(Optional.of(checkedResultModel), actual);

        verify(paymentContextParser, times(0)).parse(any());
        verify(templateVisitor, times(1)).visit(parseContext, paymentModel);
        verify(checkedResultFactory, times(1))
                .createCheckedResultWithNotifications(ACCEPT_TEMPLATE_STRING, resultModel);
    }

}
