package com.rbkmoney.fraudbusters.util;

import com.rbkmoney.fraudbusters.converter.TrustConditionToConditionConverter;
import com.rbkmoney.fraudo.model.TrustCondition;
import com.rbkmoney.trusted.tokens.Condition;
import com.rbkmoney.trusted.tokens.ConditionTemplate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static com.rbkmoney.fraudbusters.factory.TestObjectsFactory.createCondition;
import static com.rbkmoney.fraudbusters.factory.TestObjectsFactory.createTrustCondition;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConditionTemplateFactoryTest {

    private ConditionTemplateFactory factory;

    @Mock
    private TrustConditionToConditionConverter trustConditionToConditionConverter;

    @BeforeEach
    void setUp() {
        factory = new ConditionTemplateFactory(trustConditionToConditionConverter);
    }

    @Test
    void createConditionTemplateWithPaymentsConditions() {
        List<Condition> paymentsConditions = List.of(createCondition(2), createCondition());
        when(trustConditionToConditionConverter.convertBatch(anyList()))
                .thenReturn(paymentsConditions);

        List<TrustCondition> paymentsTrustConditions =
                List.of(createTrustCondition(2), createTrustCondition(null));
        ConditionTemplate conditionTemplate = factory.createConditionTemplate(paymentsTrustConditions, null);

        verify(trustConditionToConditionConverter, times(1)).convertBatch(paymentsTrustConditions);

        assertEquals(paymentsConditions, conditionTemplate.getPaymentsConditions().getConditions());
        assertFalse(conditionTemplate.isSetWithdrawalsConditions());
    }

    @Test
    void createConditionTemplateWithWithdrawalsConditions() {
        List<Condition> withdrawalsConditions = List.of(createCondition(3), createCondition());
        when(trustConditionToConditionConverter.convertBatch(anyList()))
                .thenReturn(withdrawalsConditions);

        List<TrustCondition> withdrawalsTrustConditions =
                List.of(createTrustCondition(3), createTrustCondition(null));
        ConditionTemplate conditionTemplate = factory.createConditionTemplate(null, withdrawalsTrustConditions);

        verify(trustConditionToConditionConverter, times(1)).convertBatch(withdrawalsTrustConditions);

        assertFalse(conditionTemplate.isSetPaymentsConditions());
        assertEquals(withdrawalsConditions, conditionTemplate.getWithdrawalsConditions().getConditions());
    }

    @Test
    void createConditionTemplateWithWithdrawalsAndPaymentsConditions() {
        List<Condition> paymentsConditions = List.of(createCondition(2), createCondition());
        List<Condition> withdrawalsConditions = List.of(createCondition(3), createCondition());
        when(trustConditionToConditionConverter.convertBatch(anyList()))
                .thenReturn(paymentsConditions)
                .thenReturn(withdrawalsConditions);

        List<TrustCondition> paymentsTrustConditions =
                List.of(createTrustCondition(2), createTrustCondition(null));
        List<TrustCondition> withdrawalsTrustConditions =
                List.of(createTrustCondition(3), createTrustCondition(null));
        ConditionTemplate conditionTemplate =
                factory.createConditionTemplate(paymentsTrustConditions, withdrawalsTrustConditions);

        verify(trustConditionToConditionConverter, times(1)).convertBatch(paymentsTrustConditions);
        verify(trustConditionToConditionConverter, times(1)).convertBatch(withdrawalsTrustConditions);

        assertEquals(paymentsConditions, conditionTemplate.getPaymentsConditions().getConditions());
        assertEquals(withdrawalsConditions, conditionTemplate.getWithdrawalsConditions().getConditions());
    }
}
