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
                .map(ruleResult -> {
                    ConcreteCheckResult concreteCheckResult = new ConcreteCheckResult();
                    concreteCheckResult.setResultStatus(resultStatusConverter.convert(ruleResult.getResultStatus()));
                    concreteCheckResult.setRuleChecked(ruleResult.getRuleChecked());
                    concreteCheckResult.setNotificationsRule(ResultUtils.getNotifications(resultModel));
                    CheckResult checkResult = new CheckResult();
                    checkResult.setConcreteCheckResult(concreteCheckResult);
                    checkResult.setCheckedTemplate(templateString);
                    return checkResult;
                })
                .orElse(createDefaultCheckResult(templateString, resultModel));
    }

    private CheckResult createDefaultCheckResult(String templateString, ResultModel resultModel) {
        ConcreteCheckResult concreteCheckResult = new ConcreteCheckResult();
        concreteCheckResult.setNotificationsRule(ResultUtils.getNotifications(resultModel));
        CheckResult checkResult = new CheckResult();
        checkResult.setCheckedTemplate(templateString);

        return checkResult;
    }

}
