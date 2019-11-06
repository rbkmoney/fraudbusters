package com.rbkmoney.fraudbusters.fraud.resolver;

import com.rbkmoney.fraudbusters.constant.EventField;
import com.rbkmoney.fraudbusters.constant.EventP2PField;
import com.rbkmoney.fraudbusters.exception.UnknownFieldException;
import com.rbkmoney.fraudbusters.fraud.constant.P2PCheckedField;
import com.rbkmoney.fraudbusters.fraud.constant.PaymentCheckedField;
import com.rbkmoney.fraudbusters.fraud.model.P2PModel;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DbP2pFieldResolver {

    @NotNull
    public List<FieldModel> resolveListFields(P2PModel model, List<P2PCheckedField> list) {
        if (list != null) {
            return list.stream()
                    .map(field -> resolve(field, model))
                    .collect(Collectors.toList());
        }
        return new ArrayList<>();
    }

    public FieldModel resolve(P2PCheckedField field, P2PModel model) {
        if (field == null) {
            throw new UnknownFieldException();
        }
        switch (field) {
            case IP:
                return new FieldModel(EventP2PField.ip.name(), model.getIp());
            case EMAIL:
                return new FieldModel(EventP2PField.email.name(), model.getEmail());
            case BIN:
                return new FieldModel(EventP2PField.bin.name(), model.getSender().getBin());
            case FINGERPRINT:
                return new FieldModel(EventP2PField.fingerprint.name(), model.getFingerprint());
            case IDENTITY_ID:
                return new FieldModel(EventP2PField.identityId.name(), model.getIdentityId());
            case CARD_TOKEN_FROM:
                return new FieldModel(EventP2PField.cardTokenFrom.name(), model.getSender().getCardToken());
            case CARD_TOKEN_TO:
                return new FieldModel(EventField.cardToken.name(), model.getReceiver().getCardToken());
            case PAN:
                return new FieldModel(EventField.maskedPan.name(), model.getSender().getPan());
            default:
                throw new UnknownFieldException();
        }
    }

    public String resolve(PaymentCheckedField field) {
        if (field == null) {
            throw new UnknownFieldException();
        }
        switch (field) {
            case IP:
                return EventField.ip.name();
            case EMAIL:
                return EventField.email.name();
            case BIN:
                return EventField.bin.name();
            case PAN:
                return EventField.maskedPan.name();
            case FINGERPRINT:
                return EventField.fingerprint.name();
            case PARTY_ID:
                return EventField.partyId.name();
            case SHOP_ID:
                return EventField.shopId.name();
            case CARD_TOKEN:
                return EventField.cardToken.name();
            default:
                throw new UnknownFieldException();
        }
    }

}
