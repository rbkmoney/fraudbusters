package com.rbkmoney.fraudbusters.converter;

import com.rbkmoney.fraudo.model.TrustCondition;
import com.rbkmoney.trusted.tokens.Condition;
import com.rbkmoney.trusted.tokens.YearsOffset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.rbkmoney.fraudbusters.factory.TestObjectsFactory.createTrustCondition;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TrustConditionToConditionConverterTest {

    private TrustConditionToConditionConverter converter;

    @Mock
    private IntegerToYearsOffsetConverter integerToYearsOffsetConverter;

    @BeforeEach
    void setUp() {
        converter = new TrustConditionToConditionConverter(integerToYearsOffsetConverter);
    }

    @Test
    void convert() {
        when(integerToYearsOffsetConverter.convert(anyInt())).thenReturn(YearsOffset.current_with_last_years);

        TrustCondition trustCondition = createTrustCondition(2);
        Condition condition = converter.convert(trustCondition);
        verify(integerToYearsOffsetConverter, times(1))
                .convert(trustCondition.getTransactionsYearsOffset());

        assertEquals(trustCondition.getTransactionsCount().intValue(), condition.getCount());
        assertEquals(trustCondition.getTransactionsCurrency(), condition.getCurrencySymbolicCode());
        assertEquals(trustCondition.getTransactionsYearsOffset(), condition.getYearsOffset().getValue());
        assertEquals(trustCondition.getTransactionsSum().longValue(), condition.getSum());
    }

    @Test
    void convertBatch() {
        when(integerToYearsOffsetConverter.convert(anyInt())).thenReturn(YearsOffset.current_with_last_years);

        TrustCondition trustCondition = createTrustCondition(2);
        TrustCondition trustConditionNoSum = createTrustCondition(null);
        Map<Integer, Condition> conditions =
                converter.convertBatch(List.of(trustCondition, trustConditionNoSum)).stream()
                        .collect(Collectors.toMap(
                                Condition::getCount,
                                condition -> condition
                        ));

        verify(integerToYearsOffsetConverter, times(2))
                .convert(trustCondition.getTransactionsYearsOffset());

        Condition condition = conditions.get(trustCondition.getTransactionsCount());
        assertEquals(trustCondition.getTransactionsCount().intValue(), condition.getCount());
        assertEquals(trustCondition.getTransactionsCurrency(), condition.getCurrencySymbolicCode());
        assertEquals(trustCondition.getTransactionsYearsOffset(), condition.getYearsOffset().getValue());
        assertEquals(trustCondition.getTransactionsSum().longValue(), condition.getSum());

        condition = conditions.get(trustConditionNoSum.getTransactionsCount());
        assertEquals(trustConditionNoSum.getTransactionsCount().intValue(), condition.getCount());
        assertEquals(trustConditionNoSum.getTransactionsCurrency(), condition.getCurrencySymbolicCode());
        assertEquals(trustConditionNoSum.getTransactionsYearsOffset(), condition.getYearsOffset().getValue());
        assertFalse(condition.isSetSum());
    }
}
