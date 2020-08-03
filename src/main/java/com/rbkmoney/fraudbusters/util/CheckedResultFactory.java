package com.rbkmoney.fraudbusters.util;

import com.rbkmoney.fraudbusters.domain.CheckedResultModel;
import com.rbkmoney.fraudbusters.domain.ConcreteResultModel;
import com.rbkmoney.fraudo.model.ResultModel;
import com.rbkmoney.fraudo.model.RuleResult;
import com.rbkmoney.fraudo.utils.ResultUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
public class CheckedResultFactory {

    @NonNull
    public Optional<CheckedResultModel> createCheckedResult(String templateKey, ResultModel resultModel) {
        Optional<RuleResult> firstNotNotifyStatus = ResultUtils.findFirstNotNotifyStatus(resultModel);
        if (firstNotNotifyStatus.isPresent()) {
            log.info("createCheckedResult resultModel: {}", resultModel);
            CheckedResultModel checkedResultModel = new CheckedResultModel();
            ConcreteResultModel concreteResultModel = new ConcreteResultModel();
            RuleResult ruleResult = firstNotNotifyStatus.get();
            concreteResultModel.setResultStatus(ruleResult.getResultStatus());
            concreteResultModel.setRuleChecked(ruleResult.getRuleChecked());
            concreteResultModel.setNotificationsRule(ResultUtils.getNotifications(resultModel));
            checkedResultModel.setResultModel(concreteResultModel);
            checkedResultModel.setCheckedTemplate(templateKey);
            return Optional.of(checkedResultModel);
        }
        return Optional.empty();
    }

}
