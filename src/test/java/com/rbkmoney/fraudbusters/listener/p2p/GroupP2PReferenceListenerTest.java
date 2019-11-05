package com.rbkmoney.fraudbusters.listener.p2p;

import com.rbkmoney.fraudbusters.exception.UnknownReferenceException;
import com.rbkmoney.fraudbusters.listener.p2p.GroupP2PReferenceListener;
import com.rbkmoney.fraudbusters.template.pool.GroupReferencePoolImpl;
import com.rbkmoney.fraudbusters.template.pool.Pool;
import com.rbkmoney.fraudbusters.util.BeanUtil;
import com.rbkmoney.fraudbusters.util.ReferenceKeyGenerator;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class GroupP2PReferenceListenerTest {

    private static final String GROUP_REF_1 = "group_ref_1";
    private static final String IDENTITY_ID = "identity_id";

    private Pool<String> groupP2PReferencePoolImpl;
    private GroupP2PReferenceListener groupP2PReferenceListener;

    @Before
    public void init() {
        groupP2PReferencePoolImpl = new GroupReferencePoolImpl();
        groupP2PReferenceListener = new GroupP2PReferenceListener(groupP2PReferencePoolImpl);
    }

    @Test(expected = UnknownReferenceException.class)
    public void listen() {
        groupP2PReferenceListener.listen(BeanUtil.createP2PGroupReferenceCommand(IDENTITY_ID, GROUP_REF_1));
        String ref = groupP2PReferencePoolImpl.get(ReferenceKeyGenerator.generateTemplateKeyByList(IDENTITY_ID));
        Assert.assertEquals(GROUP_REF_1, ref);

        groupP2PReferenceListener.listen(BeanUtil.createP2PGroupReferenceCommand(null, GROUP_REF_1));
        ref = groupP2PReferencePoolImpl.get(ReferenceKeyGenerator.generateTemplateKeyByList(IDENTITY_ID));
        Assert.assertEquals(GROUP_REF_1, ref);
    }
}