package com.rbkmoney.fraudbusters.util;

import com.rbkmoney.fraudbusters.domain.CheckedResultModel;
import com.rbkmoney.fraudbusters.domain.ConcreteResultModel;
import com.rbkmoney.fraudo.model.ResultModel;
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
        return ResultUtils.findFirstNotNotifyStatus(resultModel).map(ruleResult -> {
            log.info("createCheckedResult resultModel: {}", resultModel);
            ConcreteResultModel concreteResultModel = new ConcreteResultModel();
            concreteResultModel.setResultStatus(ruleResult.getResultStatus());
            concreteResultModel.setRuleChecked(ruleResult.getRuleChecked());
            concreteResultModel.setNotificationsRule(ResultUtils.getNotifications(resultModel));

            CheckedResultModel checkedResultModel = new CheckedResultModel();
            checkedResultModel.setResultModel(concreteResultModel);
            checkedResultModel.setCheckedTemplate(templateKey);
            return checkedResultModel;
        });
    }

    @NonNull
    public CheckedResultModel createCheckedResultWithNotifications(String templateKey, ResultModel resultModel) {
        return createCheckedResult(templateKey, resultModel)
                .orElseGet(() -> createNotificationOnlyResultModel(templateKey, resultModel));
    }

    private CheckedResultModel createNotificationOnlyResultModel(String templateKey, ResultModel resultModel) {
        ConcreteResultModel concreteResultModel = new ConcreteResultModel();
        concreteResultModel.setNotificationsRule(ResultUtils.getNotifications(resultModel));
        CheckedResultModel checkedResultModel = new CheckedResultModel();
        checkedResultModel.setResultModel(concreteResultModel);
        checkedResultModel.setCheckedTemplate(templateKey);

        return checkedResultModel;
    }

}
