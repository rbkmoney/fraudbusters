package com.rbkmoney.fraudbusters.stream;

import com.rbkmoney.fraudbusters.domain.CheckedResultModel;
import com.rbkmoney.fraudbusters.template.pool.Pool;
import com.rbkmoney.fraudo.FraudoParser;
import com.rbkmoney.fraudo.constant.ResultStatus;
import com.rbkmoney.fraudo.model.PaymentModel;
import com.rbkmoney.fraudo.model.ResultModel;
import com.rbkmoney.fraudo.visitor.impl.FastFraudVisitorImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class RuleApplierImpl implements RuleApplier {

    private final FastFraudVisitorImpl paymentRuleVisitor;

    private final Pool<FraudoParser.ParseContext> templatePool;

    @Override
    public Optional<CheckedResultModel> apply(PaymentModel paymentModel, String templateKey) {
        FraudoParser.ParseContext parseContext = templatePool.get(templateKey);
        if (parseContext != null) {
            ResultModel resultModel = (ResultModel) paymentRuleVisitor.visit(parseContext, paymentModel);
            if (!ResultStatus.NORMAL.equals(resultModel.getResultStatus())) {
                log.info("applyRules resultModel: {}", resultModel);
                CheckedResultModel checkedResultModel = new CheckedResultModel();
                checkedResultModel.setResultModel(resultModel);
                checkedResultModel.setCheckedTemplate(templateKey);
                return Optional.of(checkedResultModel);
            }
        }
        return Optional.empty();
    }

    @Override
    public Optional<CheckedResultModel> applyForAny(PaymentModel paymentModel, List<String> templateKeys) {
        if (templateKeys != null) {
            for (String templateKey : templateKeys) {
                Optional<CheckedResultModel> result = apply(paymentModel, templateKey);
                if (result.isPresent()) {
                    return result;
                }
            }
        }
        return Optional.empty();
    }

}
