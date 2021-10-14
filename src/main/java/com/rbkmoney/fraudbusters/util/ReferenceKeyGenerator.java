package com.rbkmoney.fraudbusters.util;

import com.rbkmoney.damsel.fraudbusters.TemplateReference;
import com.rbkmoney.fraudbusters.constant.TemplateLevel;
import com.rbkmoney.fraudbusters.exception.UnknownReferenceException;
import org.springframework.util.StringUtils;

public class ReferenceKeyGenerator {

    private static final String SEPARATOR = "_";

    public static String generateTemplateKey(TemplateReference reference) {
        if (reference.is_global) {
            return TemplateLevel.GLOBAL.name();
        }
        return generateTemplateKey(reference.party_id, reference.shop_id);
    }

    @Deprecated
    public static String generateTemplateKey(String partyId, String shopId) {
        if (StringUtils.isEmpty(shopId) && !StringUtils.isEmpty(partyId)) {
            return partyId;
        } else if (!StringUtils.isEmpty(shopId) && !StringUtils.isEmpty(partyId)) {
            return partyId + SEPARATOR + shopId;
        }
        return TemplateLevel.DEFAULT.name();
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
                if (!StringUtils.isEmpty(id)) {
                    resultKeyBuilder
                            .append(SEPARATOR)
                            .append(id);
                }
            }
        }
        return resultKeyBuilder;
    }

}
