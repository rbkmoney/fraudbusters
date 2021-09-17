package com.rbkmoney.fraudbusters.listener;

import com.rbkmoney.damsel.fraudbusters.PriorityId;
import com.rbkmoney.fraudbusters.listener.payment.GroupListener;
import com.rbkmoney.fraudbusters.pool.Pool;
import com.rbkmoney.fraudbusters.pool.PoolImpl;
import com.rbkmoney.fraudbusters.util.BeanUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class GroupListenerTest {

    public static final String FIRST_TEMPL = "first_templ";
    public static final String SECOND_TEMPL = "second_templ";
    private static final String GROUP_1 = "group_1";
    private Pool<List<String>> groupPoolImpl;
    private GroupListener groupListener;

    @BeforeEach
    public void init() {
        groupPoolImpl = new PoolImpl<>("group");
        groupListener = new GroupListener(groupPoolImpl);
    }

    @Test
    public void listenEmptyTemplateIds() {
        groupListener.listen(BeanUtil.createGroupCommand(GROUP_1, List.of()));
        List<String> strings = groupPoolImpl.get(GROUP_1);
        assertTrue(strings.isEmpty());
    }

    @Test
    public void listenTemplateIds() {
        //check sorts
        groupListener.listen(BeanUtil.createGroupCommand(GROUP_1, List.of(
                new PriorityId()
                        .setPriority(2L)
                        .setId(SECOND_TEMPL),
                new PriorityId()
                        .setId(FIRST_TEMPL)
                        .setPriority(1L)
        )));

        List<String> templateIds = groupPoolImpl.get(GROUP_1);
        assertFalse(templateIds.isEmpty());

        assertEquals(FIRST_TEMPL, templateIds.get(0));
        assertEquals(SECOND_TEMPL, templateIds.get(1));

        //check rewrite
        groupListener.listen(BeanUtil.createGroupCommand(GROUP_1, List.of()));
        templateIds = groupPoolImpl.get(GROUP_1);
        assertTrue(templateIds.isEmpty());

        //check rewrite
        groupListener.listen(BeanUtil.deleteGroupCommand(GROUP_1, List.of()));
        templateIds = groupPoolImpl.get(GROUP_1);
        assertNull(templateIds);
    }
}
