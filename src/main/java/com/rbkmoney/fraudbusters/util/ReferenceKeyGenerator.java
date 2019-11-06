package com.rbkmoney.fraudbusters.util;

import com.rbkmoney.damsel.fraudbusters.P2PReference;
import com.rbkmoney.damsel.fraudbusters.TemplateReference;
import com.rbkmoney.fraudbusters.constant.TemplateLevel;
import com.rbkmoney.fraudbusters.exception.UnknownReferenceException;
import org.springframework.util.StringUtils;
import org.testcontainers.shaded.io.netty.util.internal.StringUtil;

public class ReferenceKeyGenerator {

    private static final String SEPARATOR = "_";

    public static String generateTemplateKey(TemplateReference reference) {
        if (reference.is_global) {
            return TemplateLevel.GLOBAL.name();
        }
        return generateTemplateKey(reference.party_id, reference.shop_id);
    }

    public static String generateP2PTemplateKey(P2PReference reference) {
        if (reference.is_global) {
            return TemplateLevel.GLOBAL.name();
        }
        return generateTemplateKeyByList(reference.identity_id);
    }

    @Deprecated
    public static String generateTemplateKey(String partyId, String shopId) {
        if (StringUtil.isNullOrEmpty(shopId) && !StringUtil.isNullOrEmpty(partyId)) {
            return partyId;
        } else if (!StringUtil.isNullOrEmpty(shopId) && !StringUtil.isNullOrEmpty(partyId)) {
            return partyId + SEPARATOR + shopId;
        }
        throw new UnknownReferenceException();
    }

    public static String generateTemplateKeyByList(String... ids) {
        if (ids == null || ids.length == 0 || (ids.length == 1 && StringUtils.isEmpty(ids[0]))) {
            throw new UnknownReferenceException();
        }
        StringBuilder resultKeyBuilder = initBuilder(ids);
        if (resultKeyBuilder != null) {
            return resultKeyBuilder.toString();
        }
        throw new UnknownReferenceException();
    }

    private static StringBuilder initBuilder(String[] ids) {
        StringBuilder resultKeyBuilder = null;
        for (String id : ids) {
            if (resultKeyBuilder == null) {
                resultKeyBuilder = new StringBuilder()
                        .append(id);
            } else {
                if (!StringUtil.isNullOrEmpty(id)) {
                    resultKeyBuilder
                            .append(SEPARATOR)
                            .append(id);
                }
            }
        }
        return resultKeyBuilder;
    }

}
