package com.rbkmoney.fraudbusters.fraud.resolver;

import com.rbkmoney.fraudbusters.constant.EventField;
import com.rbkmoney.fraudbusters.exception.UnknownFieldException;
import com.rbkmoney.fraudo.constant.CheckedField;
import com.rbkmoney.fraudo.model.FraudModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FieldResolver {

    @NotNull
    public List<FieldModel> resolveListFields(FraudModel fraudModel, List<CheckedField> list) {
        if (list != null) {
            return list.stream()
                    .map(field -> resolve(field, fraudModel))
                    .collect(Collectors.toList());
        }
        return new ArrayList<>();
    }

    public FieldModel resolve(CheckedField field, FraudModel fraudModel) {
        if (field == null) {
            throw new UnknownFieldException();
        }
        switch (field) {
            case IP:
                return new FieldModel(EventField.ip.name(), fraudModel.getIp());
            case EMAIL:
                return new FieldModel(EventField.email.name(), fraudModel.getEmail());
            case BIN:
                return new FieldModel(EventField.bin.name(), fraudModel.getBin());
            case FINGERPRINT:
                return new FieldModel(EventField.fingerprint.name(), fraudModel.getFingerprint());
            case PARTY_ID:
                return new FieldModel(EventField.partyId.name(), fraudModel.getPartyId());
            case SHOP_ID:
                return new FieldModel(EventField.shopId.name(), fraudModel.getShopId());
            case CARD_TOKEN:
                return new FieldModel(EventField.cardToken.name(), fraudModel.getCardToken());
            case PAN:
                return new FieldModel(EventField.maskedPan.name(), fraudModel.getPan());
            default:
                throw new UnknownFieldException();
        }
    }

    public String resolve(CheckedField field) {
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

    @Data
    @AllArgsConstructor
    public class FieldModel {
        private String name;
        private String value;
    }
}
