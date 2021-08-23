package com.rbkmoney.fraudbusters.converter;

import com.rbkmoney.damsel.fraudbusters.CheckResult;
import com.rbkmoney.damsel.fraudbusters.ConcreteCheckResult;
import com.rbkmoney.fraudbusters.domain.CheckedResultModel;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class CheckedResultModelToCheckResultConverter implements Converter<CheckedResultModel, CheckResult> {

    private final ResultStatusConverter resultStatusConverter;

    @Override
    public CheckResult convert(CheckedResultModel model) {
        return new CheckResult()
                .setCheckedTemplate(model.getCheckedTemplate())
                .setConcreteCheckResult(new ConcreteCheckResult()
                        .setResultStatus(resultStatusConverter.convert(model.getResultModel().getResultStatus()))
                        .setRuleChecked(model.getResultModel().getRuleChecked())
                        .setNotificationsRule(model.getResultModel().getNotificationsRule()));
    }

}
