package com.rbkmoney.fraudbusters.stream;

import com.rbkmoney.fraudbusters.constant.TemplateLevel;
import com.rbkmoney.fraudbusters.domain.CheckedResultModel;
import com.rbkmoney.fraudbusters.fraud.model.P2PModel;
import com.rbkmoney.fraudbusters.template.pool.Pool;
import com.rbkmoney.fraudo.constant.ResultStatus;
import com.rbkmoney.fraudo.model.ResultModel;
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

    private final RuleApplier<P2PModel> ruleApplier;
    private final Pool<List<String>> groupPoolImpl;
    private final Pool<String> referencePoolImpl;
    private final Pool<String> groupReferencePoolImpl;

    @Override
    public CheckedResultModel visit(P2PModel p2PModel) {
        String identityId = p2PModel.getIdentityId();

        return ruleApplier.apply(p2PModel, referencePoolImpl.get(TemplateLevel.GLOBAL.name()))
                .orElse(ruleApplier.applyForAny(p2PModel, groupPoolImpl.get(groupReferencePoolImpl.get(identityId)))
                        .orElse(ruleApplier.apply(p2PModel, referencePoolImpl.get(identityId))
                                .orElse(createDefaultResult())));
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
