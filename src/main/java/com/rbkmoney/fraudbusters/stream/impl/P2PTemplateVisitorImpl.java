package com.rbkmoney.fraudbusters.stream.impl;

import com.rbkmoney.fraudbusters.constant.TemplateLevel;
import com.rbkmoney.fraudbusters.domain.CheckedResultModel;
import com.rbkmoney.fraudbusters.domain.ConcreteResultModel;
import com.rbkmoney.fraudbusters.fraud.model.P2PModel;
import com.rbkmoney.fraudbusters.pool.Pool;
import com.rbkmoney.fraudbusters.stream.RuleApplier;
import com.rbkmoney.fraudbusters.stream.TemplateVisitor;
import com.rbkmoney.fraudo.constant.ResultStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class P2PTemplateVisitorImpl implements TemplateVisitor<P2PModel, CheckedResultModel> {

    private static final String RULE_NOT_CHECKED = "RULE_NOT_CHECKED";

    private final RuleApplier<P2PModel> ruleP2PApplier;
    private final Pool<List<String>> groupP2PPoolImpl;
    private final Pool<String> referenceP2PPoolImpl;
    private final Pool<String> groupReferenceP2PPoolImpl;

    @Override
    public CheckedResultModel visit(P2PModel p2PModel) {
        String identityId = p2PModel.getIdentityId();
        return ruleP2PApplier.apply(p2PModel, referenceP2PPoolImpl.get(TemplateLevel.GLOBAL.name()))
                .orElse(ruleP2PApplier.applyForAny(p2PModel, groupP2PPoolImpl.get(groupReferenceP2PPoolImpl.get(identityId)))
                        .orElse(ruleP2PApplier.apply(p2PModel, referenceP2PPoolImpl.get(identityId))
                                .orElse(createDefaultResult())));
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
