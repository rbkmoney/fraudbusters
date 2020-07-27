package com.rbkmoney.fraudbusters.stream;

import com.rbkmoney.fraudbusters.domain.CheckedResultModel;
import com.rbkmoney.fraudbusters.fraud.model.PaymentModel;
import com.rbkmoney.fraudbusters.stream.impl.RuleApplierImpl;
import com.rbkmoney.fraudbusters.stream.impl.TemplateVisitorImpl;
import com.rbkmoney.fraudbusters.template.pool.Pool;
import com.rbkmoney.fraudbusters.template.pool.PoolImpl;
import com.rbkmoney.fraudbusters.util.ReferenceKeyGenerator;
import com.rbkmoney.fraudo.constant.ResultStatus;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

public class TemplateVisitorImplTest {

    public static final String PARTY_ID = "party_id";
    public static final String TEMPLATE_1 = "template_1";
    public static final String GROUP_1 = "group_1";
    public static final String TRUE_TEMPL = "true_templ";

    @Mock
    private RuleApplierImpl ruleApplier;

    private Pool<List<String>> groupPoolImpl;
    private Pool<String> referencePoolImpl;
    private Pool<String> groupReferencePoolImpl;

    TemplateVisitorImpl templateVisitor;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);

        groupPoolImpl = new PoolImpl<>("group");
        referencePoolImpl = new PoolImpl<>("reference");
        groupReferencePoolImpl = new PoolImpl<>("group-reference");

        templateVisitor = new TemplateVisitorImpl(ruleApplier, groupPoolImpl, referencePoolImpl, groupReferencePoolImpl);
    }

    @Test
    public void visit() {
        //check empty pools
        PaymentModel paymentModel = new PaymentModel();
        paymentModel.setPartyId(PARTY_ID);
        CheckedResultModel visit = templateVisitor.visit(paymentModel);

        Assert.assertEquals("RULE_NOT_CHECKED", visit.getCheckedTemplate());
        Assert.assertEquals(ResultStatus.THREE_DS, visit.getResultModel().getResultStatus());

        //check group party pool
        List<String> templateIds = List.of(TEMPLATE_1);
        groupPoolImpl.add(GROUP_1, templateIds);
        String key = ReferenceKeyGenerator.generateTemplateKey(PARTY_ID, null);
        groupReferencePoolImpl.add(key, GROUP_1);
        CheckedResultModel checkedResultModel = new CheckedResultModel();
        checkedResultModel.setCheckedTemplate(TRUE_TEMPL);
        Mockito.when(ruleApplier.applyForAny(paymentModel, templateIds)).thenReturn(Optional.of(checkedResultModel));

        visit = templateVisitor.visit(paymentModel);
        Assert.assertEquals(TRUE_TEMPL, visit.getCheckedTemplate());

        Mockito.when(ruleApplier.applyForAny(paymentModel, templateIds)).thenReturn(Optional.empty());

        visit = templateVisitor.visit(paymentModel);
        Assert.assertEquals("RULE_NOT_CHECKED", visit.getCheckedTemplate());
        Assert.assertEquals(ResultStatus.THREE_DS, visit.getResultModel().getResultStatus());

        //check party pool
        referencePoolImpl.add(key, TEMPLATE_1);

        Mockito.when(ruleApplier.apply(paymentModel, TEMPLATE_1))
                .thenReturn(Optional.of(checkedResultModel));

        visit = templateVisitor.visit(paymentModel);
        Assert.assertEquals(TRUE_TEMPL, visit.getCheckedTemplate());
    }
}