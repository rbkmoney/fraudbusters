package com.rbkmoney.fraudbusters.stream;

import com.rbkmoney.fraudbusters.domain.CheckedResultModel;
import com.rbkmoney.fraudbusters.domain.ConcreteResultModel;
import com.rbkmoney.fraudbusters.fraud.model.PaymentModel;
import com.rbkmoney.fraudbusters.template.pool.time.TimePool;
import com.rbkmoney.fraudo.model.ResultModel;
import com.rbkmoney.fraudo.model.RuleResult;
import com.rbkmoney.fraudo.utils.ResultUtils;
import com.rbkmoney.fraudo.visitor.TemplateVisitor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.antlr.v4.runtime.ParserRuleContext;

import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
public class FullRuleApplierImpl implements RuleApplier<PaymentModel> {

    private final TemplateVisitor<PaymentModel, ResultModel> templateVisitor;

    private final TimePool<ParserRuleContext> templatePool;

    @Override
    public Optional<CheckedResultModel> apply(PaymentModel model, String templateKey) {
        ParserRuleContext parseContext = templatePool.get(templateKey, model.getTimestamp());
        if (parseContext != null) {
            ResultModel resultModel = templateVisitor.visit(parseContext, model);
            Optional<RuleResult> firstNotNotifyStatus = ResultUtils.findFirstNotNotifyStatus(resultModel);
            if (firstNotNotifyStatus.isPresent()) {
                log.info("applyRules resultModel: {}", resultModel);
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
        }
        return Optional.empty();
    }

    @Override
    public Optional<CheckedResultModel> applyForAny(PaymentModel model, List<String> templateKeys) {
        if (templateKeys != null) {
            for (String templateKey : templateKeys) {
                Optional<CheckedResultModel> result = apply(model, templateKey);
                if (result.isPresent()) {
                    return result;
                }
            }
        }
        return Optional.empty();
    }

}
