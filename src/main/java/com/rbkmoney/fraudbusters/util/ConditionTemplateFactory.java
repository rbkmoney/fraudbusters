package com.rbkmoney.fraudbusters.util;

import com.rbkmoney.fraudbusters.converter.TrustConditionToConditionConverter;
import com.rbkmoney.fraudo.model.TrustCondition;
import com.rbkmoney.trusted.tokens.ConditionTemplate;
import com.rbkmoney.trusted.tokens.PaymentsConditions;
import com.rbkmoney.trusted.tokens.WithdrawalsConditions;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;

@RequiredArgsConstructor
@Component
public class ConditionTemplateFactory {

    private final TrustConditionToConditionConverter converter;

    public ConditionTemplate createConditionTemplate(List<TrustCondition> paymentsConditions,
                                                     List<TrustCondition> withdrawalsConditions) {
        ConditionTemplate conditionTemplate = new ConditionTemplate();
        if (!CollectionUtils.isEmpty(paymentsConditions)) {
            conditionTemplate.setPaymentsConditions(new PaymentsConditions()
                    .setConditions(converter.convertBatch(paymentsConditions)));
        }
        if (!CollectionUtils.isEmpty(withdrawalsConditions)) {
            conditionTemplate.setWithdrawalsConditions(new WithdrawalsConditions()
                    .setConditions(converter.convertBatch(withdrawalsConditions)));
        }
        return conditionTemplate;
    }

}
