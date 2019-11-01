package com.rbkmoney.fraudbusters.stream;

import com.rbkmoney.fraudbusters.domain.CheckedResultModel;
import com.rbkmoney.fraudbusters.fraud.model.PaymentModel;
import com.rbkmoney.fraudbusters.template.pool.GroupPoolImpl;
import com.rbkmoney.fraudbusters.template.pool.GroupReferencePoolImpl;
import com.rbkmoney.fraudbusters.template.pool.Pool;
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
    private RuleApplier ruleApplier;

    private Pool<List<String>> groupPoolImpl;
    private Pool<String> referencePoolImpl;
    private Pool<String> groupReferencePoolImpl;

    TemplateVisitorImpl templateVisitor;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);

        groupPoolImpl = new GroupPoolImpl();
        referencePoolImpl = new GroupReferencePoolImpl();
        groupReferencePoolImpl = new GroupReferencePoolImpl();

        templateVisitor = new TemplateVisitorImpl(ruleApplier, groupPoolImpl, referencePoolImpl, groupReferencePoolImpl);
    }

    @Test
    public void visit() {
        //check empty pools
        PaymentModel fraudModel = new PaymentModel();
        fraudModel.setPartyId(PARTY_ID);
        CheckedResultModel visit = templateVisitor.visit(fraudModel);

        Assert.assertEquals("RULE_NOT_CHECKED", visit.getCheckedTemplate());
        Assert.assertEquals(ResultStatus.THREE_DS, visit.getResultModel().getResultStatus());

        //check group party pool
        List<String> templateIds = List.of(TEMPLATE_1);
        groupPoolImpl.add(GROUP_1, templateIds);
        String key = ReferenceKeyGenerator.generateTemplateKey(PARTY_ID, null);
        groupReferencePoolImpl.add(key, GROUP_1);
        CheckedResultModel checkedResultModel = new CheckedResultModel();
        checkedResultModel.setCheckedTemplate(TRUE_TEMPL);
        Mockito.when(ruleApplier.applyForAny(fraudModel, templateIds)).thenReturn(Optional.of(checkedResultModel));

        visit = templateVisitor.visit(fraudModel);
        Assert.assertEquals(TRUE_TEMPL, visit.getCheckedTemplate());

        Mockito.when(ruleApplier.applyForAny(fraudModel, templateIds)).thenReturn(Optional.empty());

        visit = templateVisitor.visit(fraudModel);
        Assert.assertEquals("RULE_NOT_CHECKED", visit.getCheckedTemplate());
        Assert.assertEquals(ResultStatus.THREE_DS, visit.getResultModel().getResultStatus());

        //check party pool
        referencePoolImpl.add(key, TEMPLATE_1);

        Mockito.when(ruleApplier.apply(fraudModel, TEMPLATE_1))
                .thenReturn(Optional.of(checkedResultModel));

        visit = templateVisitor.visit(fraudModel);
        Assert.assertEquals(TRUE_TEMPL, visit.getCheckedTemplate());
    }
}