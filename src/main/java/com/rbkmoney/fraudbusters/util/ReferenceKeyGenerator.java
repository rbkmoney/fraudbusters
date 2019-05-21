package com.rbkmoney.fraudbusters.util;

import com.rbkmoney.damsel.fraudbusters.TemplateReference;
import com.rbkmoney.fraudbusters.constant.TemplateLevel;
import com.rbkmoney.fraudbusters.exception.UnknownReferenceException;
import org.testcontainers.shaded.io.netty.util.internal.StringUtil;

public class ReferenceKeyGenerator {

    private static final String SEPARATOR = "_";

    public static String generateTemplateKey(TemplateReference reference) {
        if (reference.is_global) {
            return TemplateLevel.GLOBAL.name();
        }
        return generateTemplateKey(reference.party_id, reference.shop_id);
    }

    public static String generateTemplateKey(String partyId, String shopId) {
        if (StringUtil.isNullOrEmpty(shopId) && !StringUtil.isNullOrEmpty(partyId)) {
            return partyId;
        } else if (!StringUtil.isNullOrEmpty(shopId) && !StringUtil.isNullOrEmpty(partyId)) {
            return partyId + SEPARATOR + shopId;
        }
        throw new UnknownReferenceException();
    }

}
