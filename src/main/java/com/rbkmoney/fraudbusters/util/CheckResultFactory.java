package com.rbkmoney.fraudbusters.util;

import com.rbkmoney.damsel.fraudbusters.CheckResult;
import com.rbkmoney.damsel.fraudbusters.ConcreteCheckResult;
import com.rbkmoney.fraudbusters.converter.ResultStatusConverter;
import com.rbkmoney.fraudo.model.ResultModel;
import com.rbkmoney.fraudo.utils.ResultUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


@Slf4j
@RequiredArgsConstructor
@Service
public class CheckResultFactory {

    private final ResultStatusConverter resultStatusConverter;

    public CheckResult createCheckResult(String templateString, ResultModel resultModel) {
        log.info("createCheckResult() - templateString {}, resultModel: {}", templateString, resultModel);
        return ResultUtils.findFirstNotNotifyStatus(resultModel)
                .map(ruleResult ->
                        new CheckResult()
                                .setCheckedTemplate(templateString)
                                .setConcreteCheckResult(new ConcreteCheckResult()
                                        .setResultStatus(resultStatusConverter.convert(ruleResult.getResultStatus()))
                                        .setRuleChecked(ruleResult.getRuleChecked())
                                        .setNotificationsRule(ResultUtils.getNotifications(resultModel))))
                .orElse(new CheckResult()
                        .setCheckedTemplate(templateString)
                        .setConcreteCheckResult(new ConcreteCheckResult()
                                        .setNotificationsRule(ResultUtils.getNotifications(resultModel))
                        )
                );
    }

}
