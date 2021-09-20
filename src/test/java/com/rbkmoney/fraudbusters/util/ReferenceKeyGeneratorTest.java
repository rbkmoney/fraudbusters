package com.rbkmoney.fraudbusters.util;

import com.rbkmoney.fraudbusters.exception.UnknownReferenceException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ReferenceKeyGeneratorTest {

    @Test
    public void generateTemplateKey() {
        String key = ReferenceKeyGenerator.generateTemplateKey("partyId", "shopId");
        assertEquals("partyId_shopId", key);

        key = ReferenceKeyGenerator.generateTemplateKey("partyId", null);
        assertEquals("partyId", key);

        key = ReferenceKeyGenerator.generateTemplateKey(null, "shopId");
        assertEquals("DEFAULT", key);
    }

    @Test
    public void testGenerateTemplateKey() {
        String key = ReferenceKeyGenerator.generateTemplateKeyByList("partyId", "shopId");
        assertEquals("partyId_shopId", key);

        key = ReferenceKeyGenerator.generateTemplateKeyByList("partyId", null);
        assertEquals("partyId", key);

        assertThrows(UnknownReferenceException.class, ReferenceKeyGenerator::generateTemplateKeyByList);
    }
}
