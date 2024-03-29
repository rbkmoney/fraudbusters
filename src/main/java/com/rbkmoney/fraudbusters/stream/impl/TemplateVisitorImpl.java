package com.rbkmoney.fraudbusters.stream.impl;

import com.rbkmoney.fraudbusters.constant.TemplateLevel;
import com.rbkmoney.fraudbusters.domain.CheckedResultModel;
import com.rbkmoney.fraudbusters.domain.ConcreteResultModel;
import com.rbkmoney.fraudbusters.fraud.model.PaymentModel;
import com.rbkmoney.fraudbusters.pool.Pool;
import com.rbkmoney.fraudbusters.stream.TemplateVisitor;
import com.rbkmoney.fraudbusters.util.ReferenceKeyGenerator;
import com.rbkmoney.fraudo.constant.ResultStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class TemplateVisitorImpl implements TemplateVisitor<PaymentModel, CheckedResultModel> {

    private static final String RULE_NOT_CHECKED = "RULE_NOT_CHECKED";

    private final RuleApplierImpl<PaymentModel> ruleApplier;
    private final Pool<List<String>> groupPoolImpl;
    private final Pool<String> referencePoolImpl;
    private final Pool<String> groupReferencePoolImpl;

    @Override
    public CheckedResultModel visit(PaymentModel paymentModel) {
        log.debug("TemplateVisitorImpl visit paymentModel: {}", paymentModel);
        String partyId = paymentModel.getPartyId();
        String partyShopKey = ReferenceKeyGenerator.generateTemplateKey(partyId, paymentModel.getShopId());
        return ruleApplier.apply(paymentModel, referencePoolImpl.get(TemplateLevel.GLOBAL.name()))
                .orElseGet(() -> ruleApplier
                        .applyForAny(paymentModel, groupPoolImpl.get(groupReferencePoolImpl.get(partyId)))
                        .orElseGet(() -> ruleApplier
                                .applyForAny(paymentModel, groupPoolImpl.get(groupReferencePoolImpl.get(partyShopKey)))
                                .orElseGet(() -> ruleApplier
                                        .apply(paymentModel, referencePoolImpl.get(partyId))
                                        .orElseGet(() -> ruleApplier
                                                .apply(paymentModel, referencePoolImpl.get(partyShopKey))
                                                .orElseGet(this::createDefaultResult)))));
    }

    @NotNull
    private CheckedResultModel createDefaultResult() {
        ConcreteResultModel resultModel = new ConcreteResultModel();
        resultModel.setResultStatus(ResultStatus.THREE_DS);
        CheckedResultModel checkedResultModel = new CheckedResultModel();
        checkedResultModel.setResultModel(resultModel);
        checkedResultModel.setCheckedTemplate(RULE_NOT_CHECKED);
        return checkedResultModel;
    }

}
