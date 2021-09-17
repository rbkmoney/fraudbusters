package com.rbkmoney.fraudbusters.listener.p2p;

import com.rbkmoney.damsel.fraudbusters.Command;
import com.rbkmoney.fraudbusters.exception.UnknownReferenceException;
import com.rbkmoney.fraudbusters.pool.Pool;
import com.rbkmoney.fraudbusters.pool.PoolImpl;
import com.rbkmoney.fraudbusters.util.BeanUtil;
import com.rbkmoney.fraudbusters.util.ReferenceKeyGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class TemplateP2PReferenceListenerTest {

    private static final String GROUP_REF_1 = "group_ref_1";
    private static final String GROUP_REF_2 = "group_ref_2";

    private static final String IDENTITY_ID = "identity_id";

    private Pool<String> templateP2PReferencePoolImpl;
    private TemplateP2PReferenceListener templateP2PReferenceListener;

    @BeforeEach
    public void init() {
        templateP2PReferencePoolImpl = new PoolImpl<>("p2p-template-reference");
        templateP2PReferenceListener = new TemplateP2PReferenceListener(templateP2PReferencePoolImpl);
    }

    @Test()
    public void listen() {
        templateP2PReferenceListener.listen(BeanUtil.createP2PTemplateReferenceCommand(IDENTITY_ID, GROUP_REF_1));
        String ref = templateP2PReferencePoolImpl.get(ReferenceKeyGenerator.generateTemplateKeyByList(IDENTITY_ID));
        assertEquals(GROUP_REF_1, ref);

        Command p2PTemplateReferenceCommand = BeanUtil.createP2PTemplateReferenceCommand(IDENTITY_ID, GROUP_REF_2);
        p2PTemplateReferenceCommand.getCommandBody().getP2pReference().setIsGlobal(true);

        templateP2PReferenceListener.listen(p2PTemplateReferenceCommand);
        ref = templateP2PReferencePoolImpl.get(ReferenceKeyGenerator
                .generateP2PTemplateKey(p2PTemplateReferenceCommand.getCommandBody().getP2pReference()));
        assertEquals(GROUP_REF_2, ref);

        assertThrows(
                UnknownReferenceException.class,
                () -> templateP2PReferenceListener.listen(BeanUtil.createP2PTemplateReferenceCommand(null, GROUP_REF_1))
        );
        ref = templateP2PReferencePoolImpl.get(ReferenceKeyGenerator.generateTemplateKeyByList(IDENTITY_ID));
        assertEquals(GROUP_REF_1, ref);
    }

}
