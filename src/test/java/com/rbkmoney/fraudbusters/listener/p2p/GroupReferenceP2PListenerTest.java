package com.rbkmoney.fraudbusters.listener.p2p;

import com.rbkmoney.fraudbusters.exception.UnknownReferenceException;
import com.rbkmoney.fraudbusters.template.pool.Pool;
import com.rbkmoney.fraudbusters.template.pool.PoolImpl;
import com.rbkmoney.fraudbusters.util.BeanUtil;
import com.rbkmoney.fraudbusters.util.ReferenceKeyGenerator;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class GroupReferenceP2PListenerTest {

    private static final String GROUP_REF_1 = "group_ref_1";
    private static final String IDENTITY_ID = "identity_id";

    private Pool<String> groupP2PReferencePoolImpl;
    private GroupReferenceP2PListener groupReferenceP2PListener;

    @Before
    public void init() {
        groupP2PReferencePoolImpl = new PoolImpl<>("p2p-reference");
        groupReferenceP2PListener = new GroupReferenceP2PListener(groupP2PReferencePoolImpl);
    }

    @Test(expected = UnknownReferenceException.class)
    public void listen() {
        groupReferenceP2PListener.listen(BeanUtil.createP2PGroupReferenceCommand(IDENTITY_ID, GROUP_REF_1));
        String ref = groupP2PReferencePoolImpl.get(ReferenceKeyGenerator.generateTemplateKeyByList(IDENTITY_ID));
        Assert.assertEquals(GROUP_REF_1, ref);

        groupReferenceP2PListener.listen(BeanUtil.createP2PGroupReferenceCommand(null, GROUP_REF_1));
        ref = groupP2PReferencePoolImpl.get(ReferenceKeyGenerator.generateTemplateKeyByList(IDENTITY_ID));
        Assert.assertEquals(GROUP_REF_1, ref);
    }
}