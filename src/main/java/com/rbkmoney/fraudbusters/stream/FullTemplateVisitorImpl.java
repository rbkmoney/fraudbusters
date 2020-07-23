package com.rbkmoney.fraudbusters.stream;

import com.rbkmoney.fraudbusters.constant.TemplateLevel;
import com.rbkmoney.fraudbusters.domain.CheckedResultModel;
import com.rbkmoney.fraudbusters.domain.ConcreteResultModel;
import com.rbkmoney.fraudbusters.fraud.model.PaymentModel;
import com.rbkmoney.fraudbusters.template.pool.time.TimePool;
import com.rbkmoney.fraudbusters.util.ReferenceKeyGenerator;
import com.rbkmoney.fraudo.constant.ResultStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class FullTemplateVisitorImpl implements TemplateVisitor<PaymentModel, CheckedResultModel> {

    private static final String RULE_NOT_CHECKED = "RULE_NOT_CHECKED";

    private final RuleApplier<PaymentModel> fullRuleApplier;
    private final TimePool<List<String>> timeGroupPoolImpl;
    private final TimePool<String> timeReferencePoolImpl;
    private final TimePool<String> timeGroupReferencePoolImpl;

    @Override
    public CheckedResultModel visit(PaymentModel paymentModel) {
        String partyId = paymentModel.getPartyId();
        Long timestamp = initTimestamp(paymentModel);
        String partyShopKey = ReferenceKeyGenerator.generateTemplateKey(partyId, paymentModel.getShopId());
        return fullRuleApplier.apply(paymentModel, timeReferencePoolImpl.get(TemplateLevel.GLOBAL.name(), timestamp))
                .orElse(fullRuleApplier.applyForAny(paymentModel, timeGroupPoolImpl.get(timeGroupReferencePoolImpl.get(partyId, timestamp), timestamp))
                        .orElse(fullRuleApplier.applyForAny(paymentModel, timeGroupPoolImpl.get(timeGroupReferencePoolImpl.get(partyShopKey, timestamp), timestamp))
                                .orElse(fullRuleApplier.apply(paymentModel, timeReferencePoolImpl.get(partyId, timestamp))
                                        .orElse(fullRuleApplier.apply(paymentModel, timeReferencePoolImpl.get(partyShopKey, timestamp))
                                                .orElse(createDefaultResult())))));
    }

    private long initTimestamp(PaymentModel paymentModel) {
        return paymentModel.getTimestamp() != null ? paymentModel.getTimestamp() : Instant.now().toEpochMilli();
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
