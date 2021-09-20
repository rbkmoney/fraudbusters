package com.rbkmoney.fraudbusters.fraud.p2p.resolver;

import com.rbkmoney.fraudbusters.constant.EventP2PField;
import com.rbkmoney.fraudbusters.exception.UnknownFieldException;
import com.rbkmoney.fraudbusters.fraud.constant.P2PCheckedField;
import com.rbkmoney.fraudbusters.fraud.model.FieldModel;
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
        return switch (field) {
            case IP -> new FieldModel(EventP2PField.ip.name(), model.getIp());
            case EMAIL -> new FieldModel(EventP2PField.email.name(), model.getEmail());
            case BIN -> new FieldModel(EventP2PField.bin.name(), model.getSender().getBin());
            case FINGERPRINT -> new FieldModel(EventP2PField.fingerprint.name(), model.getFingerprint());
            case IDENTITY_ID -> new FieldModel(EventP2PField.identityId.name(), model.getIdentityId());
            case CARD_TOKEN_FROM -> new FieldModel(EventP2PField.cardTokenFrom.name(),
                    model.getSender().getCardToken());
            case CARD_TOKEN_TO -> new FieldModel(EventP2PField.cardTokenTo.name(), model.getReceiver().getCardToken());
            case PAN -> new FieldModel(EventP2PField.maskedPan.name(), model.getSender().getPan());
            default -> throw new UnknownFieldException();
        };
    }

    public String resolve(P2PCheckedField field) {
        if (field == null) {
            throw new UnknownFieldException();
        }
        return switch (field) {
            case IP -> EventP2PField.ip.name();
            case EMAIL -> EventP2PField.email.name();
            case BIN -> EventP2PField.bin.name();
            case PAN -> EventP2PField.maskedPan.name();
            case FINGERPRINT -> EventP2PField.fingerprint.name();
            case IDENTITY_ID -> EventP2PField.identityId.name();
            case CARD_TOKEN_FROM -> EventP2PField.cardTokenFrom.name();
            case CARD_TOKEN_TO -> EventP2PField.cardTokenTo.name();
            default -> throw new UnknownFieldException();
        };
    }

}
