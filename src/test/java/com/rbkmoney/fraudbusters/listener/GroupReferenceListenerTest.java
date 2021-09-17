package com.rbkmoney.fraudbusters.listener;

import com.rbkmoney.fraudbusters.listener.payment.GroupReferenceListener;
import com.rbkmoney.fraudbusters.pool.Pool;
import com.rbkmoney.fraudbusters.pool.PoolImpl;
import com.rbkmoney.fraudbusters.util.BeanUtil;
import com.rbkmoney.fraudbusters.util.ReferenceKeyGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class GroupReferenceListenerTest {

    private static final String GROUP_REF_1 = "group_ref_1";
    private static final String PARTY = "party";
    private static final String SHOP_ID = "shopId";

    private Pool<String> groupReferencePoolImpl;
    private GroupReferenceListener groupReferenceListener;

    @BeforeEach
    public void init() {
        groupReferencePoolImpl = new PoolImpl<>("group-reference");
        groupReferenceListener = new GroupReferenceListener(groupReferencePoolImpl);
    }

    @Test
    public void listen() {
        groupReferenceListener.listen(BeanUtil.createGroupReferenceCommand(PARTY, null, GROUP_REF_1));
        String ref = groupReferencePoolImpl.get(ReferenceKeyGenerator.generateTemplateKeyByList(PARTY, null));
        assertEquals(GROUP_REF_1, ref);

        groupReferenceListener.listen(BeanUtil.createGroupReferenceCommand(PARTY, SHOP_ID, GROUP_REF_1));
        ref = groupReferencePoolImpl.get(ReferenceKeyGenerator.generateTemplateKeyByList(PARTY, SHOP_ID));
        assertEquals(GROUP_REF_1, ref);

        groupReferenceListener.listen(BeanUtil.createDeleteGroupReferenceCommand(PARTY, SHOP_ID, GROUP_REF_1));
        ref = groupReferencePoolImpl.get(ReferenceKeyGenerator.generateTemplateKeyByList(PARTY, SHOP_ID));
        assertNull(ref);
    }
}
