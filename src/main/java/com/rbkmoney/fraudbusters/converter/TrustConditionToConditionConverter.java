package com.rbkmoney.fraudbusters.converter;

import com.rbkmoney.fraudo.model.TrustCondition;
import com.rbkmoney.trusted.tokens.Condition;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class TrustConditionToConditionConverter
        implements Converter<TrustCondition, Condition>, BatchConverter<TrustCondition, Condition> {

    private final IntegerToYearsOffsetConverter integerToYearsOffsetConverter;

    @Override
    public Condition convert(TrustCondition trustCondition) {
        Condition condition = new Condition()
                .setCurrencySymbolicCode(trustCondition.getTransactionsCurrency())
                .setCount(trustCondition.getTransactionsCount())
                .setYearsOffset(integerToYearsOffsetConverter.convert(trustCondition.getTransactionsYearsOffset()));
        if (trustCondition.getTransactionsSum() != null) {
            condition.setSum(trustCondition.getTransactionsSum());
        }
        return condition;
    }

}
