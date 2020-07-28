package com.rbkmoney.fraudbusters.util;

import com.rbkmoney.fraudbusters.exception.UnknownReferenceException;
import org.junit.Assert;
import org.junit.Test;

public class ReferenceKeyGeneratorTest {

    @Test
    public void generateTemplateKey() {
        String key = ReferenceKeyGenerator.generateTemplateKeyByList("partyId", "shopId");
        Assert.assertEquals("partyId_shopId", key);

        key = ReferenceKeyGenerator.generateTemplateKeyByList("partyId", null);
        Assert.assertEquals("partyId", key);

        key = ReferenceKeyGenerator.generateTemplateKeyByList(null, "shopId");
        Assert.assertEquals("DEFAULT", key);
    }

    @Test(expected = UnknownReferenceException.class)
    public void testGenerateTemplateKey() {
        String key = ReferenceKeyGenerator.generateTemplateKeyByList("partyId", "shopId");
        Assert.assertEquals("partyId_shopId", key);

        key = ReferenceKeyGenerator.generateTemplateKeyByList("partyId", null);
        Assert.assertEquals("partyId", key);

        key = ReferenceKeyGenerator.generateTemplateKeyByList();
    }
}