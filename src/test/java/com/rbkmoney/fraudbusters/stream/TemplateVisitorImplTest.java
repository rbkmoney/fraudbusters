package com.rbkmoney.fraudbusters.stream;

import com.rbkmoney.fraudbusters.domain.CheckedResultModel;
import com.rbkmoney.fraudbusters.fraud.model.PaymentModel;
import com.rbkmoney.fraudbusters.pool.Pool;
import com.rbkmoney.fraudbusters.pool.PoolImpl;
import com.rbkmoney.fraudbusters.stream.impl.RuleApplierImpl;
import com.rbkmoney.fraudbusters.stream.impl.TemplateVisitorImpl;
import com.rbkmoney.fraudbusters.util.ReferenceKeyGenerator;
import com.rbkmoney.fraudo.constant.ResultStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class TemplateVisitorImplTest {

    public static final String PARTY_ID = "party_id";
    public static final String TEMPLATE_1 = "template_1";
    public static final String GROUP_1 = "group_1";
    public static final String TRUE_TEMPL = "true_templ";
    TemplateVisitorImpl templateVisitor;

    @Mock
    private RuleApplierImpl ruleApplier;
    private Pool<List<String>> groupPoolImpl;
    private Pool<String> referencePoolImpl;
    private Pool<String> groupReferencePoolImpl;

    @BeforeEach
    public void init() {
        groupPoolImpl = new PoolImpl<>("group");
        referencePoolImpl = new PoolImpl<>("reference");
        groupReferencePoolImpl = new PoolImpl<>("group-reference");

        templateVisitor =
                new TemplateVisitorImpl(ruleApplier, groupPoolImpl, referencePoolImpl, groupReferencePoolImpl);
    }

    @Test
    public void visit() {
        //check empty pools
        PaymentModel paymentModel = new PaymentModel();
        paymentModel.setPartyId(PARTY_ID);
        CheckedResultModel visit = templateVisitor.visit(paymentModel);

        assertEquals("RULE_NOT_CHECKED", visit.getCheckedTemplate());
        assertEquals(ResultStatus.THREE_DS, visit.getResultModel().getResultStatus());

        //check group party pool
        List<String> templateIds = List.of(TEMPLATE_1);
        groupPoolImpl.add(GROUP_1, templateIds);
        String key = ReferenceKeyGenerator.generateTemplateKeyByList(PARTY_ID, null);
        groupReferencePoolImpl.add(key, GROUP_1);
        CheckedResultModel checkedResultModel = new CheckedResultModel();
        checkedResultModel.setCheckedTemplate(TRUE_TEMPL);
        Mockito.when(ruleApplier.applyForAny(paymentModel, templateIds)).thenReturn(Optional.of(checkedResultModel));

        visit = templateVisitor.visit(paymentModel);
        assertEquals(TRUE_TEMPL, visit.getCheckedTemplate());

        Mockito.when(ruleApplier.applyForAny(paymentModel, templateIds)).thenReturn(Optional.empty());

        visit = templateVisitor.visit(paymentModel);
        assertEquals("RULE_NOT_CHECKED", visit.getCheckedTemplate());
        assertEquals(ResultStatus.THREE_DS, visit.getResultModel().getResultStatus());

        //check party pool
        referencePoolImpl.add(key, TEMPLATE_1);
        Mockito.when(ruleApplier.apply(paymentModel, null)).thenReturn(Optional.empty());
        Mockito.when(ruleApplier.apply(paymentModel, TEMPLATE_1)).thenReturn(Optional.of(checkedResultModel));

        visit = templateVisitor.visit(paymentModel);
        assertEquals(TRUE_TEMPL, visit.getCheckedTemplate());
    }
}
