package com.rbkmoney.fraudbusters.listener.p2p;

import com.rbkmoney.damsel.fraudbusters.Command;
import com.rbkmoney.fraudbusters.exception.UnknownReferenceException;
import com.rbkmoney.fraudbusters.template.pool.GroupReferencePoolImpl;
import com.rbkmoney.fraudbusters.template.pool.Pool;
import com.rbkmoney.fraudbusters.util.BeanUtil;
import com.rbkmoney.fraudbusters.util.ReferenceKeyGenerator;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class TemplateP2PReferenceListenerTest {

    private static final String GROUP_REF_1 = "group_ref_1";
    private static final String GROUP_REF_2 = "group_ref_2";

    private static final String IDENTITY_ID = "identity_id";

    private Pool<String> templateP2PReferencePoolImpl;
    private TemplateP2PReferenceListener templateP2PReferenceListener;

    @Before
    public void init() {
        templateP2PReferencePoolImpl = new GroupReferencePoolImpl();
        templateP2PReferenceListener = new TemplateP2PReferenceListener(templateP2PReferencePoolImpl);
    }

    @Test(expected = UnknownReferenceException.class)
    public void listen() {
        templateP2PReferenceListener.listen(BeanUtil.createP2PTemplateReferenceCommand(IDENTITY_ID, GROUP_REF_1));
        String ref = templateP2PReferencePoolImpl.get(ReferenceKeyGenerator.generateTemplateKeyByList(IDENTITY_ID));
        Assert.assertEquals(GROUP_REF_1, ref);

        Command p2PTemplateReferenceCommand = BeanUtil.createP2PTemplateReferenceCommand(IDENTITY_ID, GROUP_REF_2);
        p2PTemplateReferenceCommand.getCommandBody().getP2pReference().setIsGlobal(true);

        templateP2PReferenceListener.listen(p2PTemplateReferenceCommand);
        ref = templateP2PReferencePoolImpl.get(ReferenceKeyGenerator
                .generateP2PTemplateKey(p2PTemplateReferenceCommand.getCommandBody().getP2pReference()));
        Assert.assertEquals(GROUP_REF_2, ref);

        templateP2PReferenceListener.listen(BeanUtil.createP2PTemplateReferenceCommand(null, GROUP_REF_1));
        ref = templateP2PReferencePoolImpl.get(ReferenceKeyGenerator.generateTemplateKeyByList(IDENTITY_ID));
        Assert.assertEquals(GROUP_REF_1, ref);
    }
}