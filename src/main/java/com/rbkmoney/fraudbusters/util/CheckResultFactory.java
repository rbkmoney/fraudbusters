package com.rbkmoney.fraudbusters.util;

import com.rbkmoney.damsel.fraudbusters.Accept;
import com.rbkmoney.damsel.fraudbusters.AcceptAndNotify;
import com.rbkmoney.damsel.fraudbusters.CheckResult;
import com.rbkmoney.damsel.fraudbusters.ConcreteCheckResult;
import com.rbkmoney.damsel.fraudbusters.Decline;
import com.rbkmoney.damsel.fraudbusters.DeclineAndNotify;
import com.rbkmoney.damsel.fraudbusters.HighRisk;
import com.rbkmoney.damsel.fraudbusters.Normal;
import com.rbkmoney.damsel.fraudbusters.Notify;
import com.rbkmoney.damsel.fraudbusters.ResultStatus;
import com.rbkmoney.damsel.fraudbusters.ThreeDs;
import com.rbkmoney.fraudo.model.ResultModel;
import com.rbkmoney.fraudo.utils.ResultUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


@Slf4j
@Service
public class CheckResultFactory {

    public CheckResult createCheckResult(String templateString, ResultModel resultModel) {
        log.info("createCheckResult() - templateString {}, resultModel: {}", templateString, resultModel);
        return ResultUtils.findFirstNotNotifyStatus(resultModel)
                .map(ruleResult -> {
                    ConcreteCheckResult concreteCheckResult = new ConcreteCheckResult();
                    concreteCheckResult.setResultStatus(mapStatus(ruleResult.getResultStatus()));
                    concreteCheckResult.setRuleChecked(ruleResult.getRuleChecked());
                    concreteCheckResult.setNotificationsRule(ResultUtils.getNotifications(resultModel));
                    CheckResult checkResult = new CheckResult();
                    checkResult.setConcreteCheckResult(concreteCheckResult);
                    checkResult.setCheckedTemplate(templateString);
                    return checkResult;
                })
                .orElse(createDefaultCheckResult(templateString, resultModel));
    }

    private ResultStatus mapStatus(com.rbkmoney.fraudo.constant.ResultStatus resultStatus) {
        ResultStatus status = new ResultStatus();
        switch (resultStatus) {
            case ACCEPT: {
                status.setAccept(new Accept());
                break;
            }
            case ACCEPT_AND_NOTIFY: {
                status.setAcceptAndNotify(new AcceptAndNotify());
                break;
            }
            case THREE_DS: {
                status.setThreeDs(new ThreeDs());
                break;
            }
            case DECLINE: {
                status.setDecline(new Decline());
                break;
            }
            case DECLINE_AND_NOTIFY: {
                status.setDeclineAndNotify(new DeclineAndNotify());
                break;
            }
            case HIGH_RISK: {
                status.setHighRisk(new HighRisk());
                break;
            }
            case NORMAL: {
                status.setNormal(new Normal());
                break;
            }
            case NOTIFY: {
                status.setNotify(new Notify());
                break;
            }
            default: {
                throw new IllegalArgumentException("Unknown ResultStatus");
            }
        }
        return status;
    }

    private CheckResult createDefaultCheckResult(String templateString, ResultModel resultModel) {
        ConcreteCheckResult concreteCheckResult = new ConcreteCheckResult();
        concreteCheckResult.setNotificationsRule(ResultUtils.getNotifications(resultModel));
        CheckResult checkResult = new CheckResult();
        checkResult.setCheckedTemplate(templateString);

        return checkResult;
    }

}
