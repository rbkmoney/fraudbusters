package com.rbkmoney.fraudbusters.stream;

import com.rbkmoney.fraudbusters.constant.TemplateLevel;
import com.rbkmoney.fraudbusters.domain.CheckedResultModel;
import com.rbkmoney.fraudbusters.template.pool.Pool;
import com.rbkmoney.fraudbusters.util.ReferenceKeyGenerator;
import com.rbkmoney.fraudo.constant.ResultStatus;
import com.rbkmoney.fraudo.model.FraudModel;
import com.rbkmoney.fraudo.model.ResultModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class TemplateVisitorImpl implements TemplateVisitor {

    private static final String RULE_NOT_CHECKED = "RULE_NOT_CHECKED";

    private final RuleApplier ruleApplier;
    private final Pool<List<String>> groupPoolImpl;
    private final Pool<String> referencePoolImpl;
    private final Pool<String> groupReferencePoolImpl;

    @Override
    public CheckedResultModel visit(FraudModel fraudModel) {
        String partyId = fraudModel.getPartyId();
        String partyShopKey = ReferenceKeyGenerator.generateTemplateKey(partyId, fraudModel.getShopId());
        return ruleApplier.apply(fraudModel, referencePoolImpl.get(TemplateLevel.GLOBAL.name()))
                .orElse(ruleApplier.applyForList(fraudModel, groupPoolImpl.get(groupReferencePoolImpl.get(partyId)))
                        .orElse(ruleApplier.applyForList(fraudModel, groupPoolImpl.get(groupReferencePoolImpl.get(partyShopKey)))
                                .orElse(ruleApplier.apply(fraudModel, referencePoolImpl.get(partyId))
                                        .orElse(ruleApplier.apply(fraudModel, referencePoolImpl.get(partyShopKey))
                                                .orElse(createDefaultResult())))));
    }

    @NotNull
    private CheckedResultModel createDefaultResult() {
        ResultModel resultModel = new ResultModel();
        resultModel.setResultStatus(ResultStatus.THREE_DS);
        CheckedResultModel checkedResultModel = new CheckedResultModel();
        checkedResultModel.setResultModel(resultModel);
        checkedResultModel.setCheckedTemplate(RULE_NOT_CHECKED);
        return checkedResultModel;
    }

}
