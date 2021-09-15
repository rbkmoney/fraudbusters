package com.rbkmoney.fraudbusters.fraud.p2p.resolver;

import com.rbkmoney.fraudbusters.fraud.constant.P2PCheckedField;
import com.rbkmoney.fraudbusters.fraud.model.P2PModel;
import com.rbkmoney.fraudo.exception.UnresolvableFieldException;
import com.rbkmoney.fraudo.resolver.FieldResolver;

public class P2PModelFieldResolver implements FieldResolver<P2PModel, P2PCheckedField> {

    @Override
    public String resolveValue(String fieldName, P2PModel model) {
        return switch (P2PCheckedField.getByValue(fieldName)) {
            case BIN -> model.getSender().getBin();
            case IP -> model.getIp();
            case FINGERPRINT -> model.getFingerprint();
            case EMAIL -> model.getEmail();
            case COUNTRY_BANK -> model.getSender().getBinCountryCode();
            case CARD_TOKEN_FROM -> model.getSender().getCardToken();
            case CARD_TOKEN_TO -> model.getReceiver().getCardToken();
            case PAN -> model.getSender().getPan();
            default -> throw new UnresolvableFieldException(fieldName);
        };
    }

    @Override
    public P2PCheckedField resolveName(String fieldName) {
        return P2PCheckedField.getByValue(fieldName);
    }

}
