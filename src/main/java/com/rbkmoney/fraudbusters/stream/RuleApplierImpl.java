package com.rbkmoney.fraudbusters.stream;

import com.rbkmoney.fraudbusters.domain.CheckedResultModel;
import com.rbkmoney.fraudbusters.domain.ConcreteResultModel;
import com.rbkmoney.fraudbusters.template.pool.Pool;
import com.rbkmoney.fraudo.model.BaseModel;
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
public class RuleApplierImpl<T extends BaseModel> implements RuleApplier<T> {

    private final TemplateVisitor<T, ResultModel> templateVisitor;

    private final Pool<ParserRuleContext> templatePool;

    @Override
    public Optional<CheckedResultModel> apply(T model, String templateKey) {
        ParserRuleContext parseContext = templatePool.get(templateKey);
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
    public Optional<CheckedResultModel> applyForAny(T model, List<String> templateKeys) {
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
