package com.rbkmoney.fraudbusters.listener.p2p;

import com.rbkmoney.fraudbusters.exception.UnknownReferenceException;
import com.rbkmoney.fraudbusters.pool.Pool;
import com.rbkmoney.fraudbusters.pool.PoolImpl;
import com.rbkmoney.fraudbusters.util.BeanUtil;
import com.rbkmoney.fraudbusters.util.ReferenceKeyGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class GroupReferenceP2PListenerTest {

    private static final String GROUP_REF_1 = "group_ref_1";
    private static final String IDENTITY_ID = "identity_id";

    private Pool<String> groupP2PReferencePoolImpl;
    private GroupReferenceP2PListener groupReferenceP2PListener;

    @BeforeEach
    public void init() {
        groupP2PReferencePoolImpl = new PoolImpl<>("p2p-reference");
        groupReferenceP2PListener = new GroupReferenceP2PListener(groupP2PReferencePoolImpl);
    }

    @Test()
    public void listen() {
        groupReferenceP2PListener.listen(BeanUtil.createP2PGroupReferenceCommand(IDENTITY_ID, GROUP_REF_1));
        String ref = groupP2PReferencePoolImpl.get(ReferenceKeyGenerator.generateTemplateKeyByList(IDENTITY_ID));

        assertEquals(GROUP_REF_1, ref);

        assertThrows(
                UnknownReferenceException.class,
                () -> groupReferenceP2PListener.listen(BeanUtil.createP2PGroupReferenceCommand(null, GROUP_REF_1))
        );

        ref = groupP2PReferencePoolImpl.get(ReferenceKeyGenerator.generateTemplateKeyByList(IDENTITY_ID));
        assertEquals(GROUP_REF_1, ref);
    }
}
