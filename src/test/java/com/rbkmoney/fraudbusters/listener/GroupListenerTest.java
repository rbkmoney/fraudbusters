package com.rbkmoney.fraudbusters.listener;

import com.rbkmoney.damsel.fraudbusters.PriorityId;
import com.rbkmoney.fraudbusters.listener.payment.GroupListener;
import com.rbkmoney.fraudbusters.pool.Pool;
import com.rbkmoney.fraudbusters.pool.PoolImpl;
import com.rbkmoney.fraudbusters.util.BeanUtil;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

public class GroupListenerTest {

    public static final String FIRST_TEMPL = "first_templ";
    public static final String SECOND_TEMPL = "second_templ";
    private static final String GROUP_1 = "group_1";
    private Pool<List<String>> groupPoolImpl;
    private GroupListener groupListener;

    @Before
    public void init() {
        groupPoolImpl = new PoolImpl<>("group");
        groupListener = new GroupListener(groupPoolImpl);
    }

    @Test
    public void listenEmptyTemplateIds() {
        groupListener.listen(BeanUtil.createGroupCommand(GROUP_1, List.of()));
        List<String> strings = groupPoolImpl.get(GROUP_1);
        Assert.assertTrue(strings.isEmpty());
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
        Assert.assertFalse(templateIds.isEmpty());

        Assert.assertEquals(FIRST_TEMPL, templateIds.get(0));
        Assert.assertEquals(SECOND_TEMPL, templateIds.get(1));

        //check rewrite
        groupListener.listen(BeanUtil.createGroupCommand(GROUP_1, List.of()));
        templateIds = groupPoolImpl.get(GROUP_1);
        Assert.assertTrue(templateIds.isEmpty());

        //check rewrite
        groupListener.listen(BeanUtil.deleteGroupCommand(GROUP_1, List.of()));
        templateIds = groupPoolImpl.get(GROUP_1);
        Assert.assertNull(templateIds);
    }
}