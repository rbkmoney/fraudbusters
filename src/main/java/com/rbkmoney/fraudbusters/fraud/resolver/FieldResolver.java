package com.rbkmoney.fraudbusters.fraud.resolver;

import com.rbkmoney.fraudbusters.constant.EventField;
import com.rbkmoney.fraudbusters.exception.UnknownFieldException;
import com.rbkmoney.fraudo.constant.CheckedField;
import com.rbkmoney.fraudo.model.FraudModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.stereotype.Service;

@Service
public class FieldResolver {

    public FieldModel resolve(CheckedField field, FraudModel fraudModel) {
        switch (field) {
            case IP:
                return new FieldModel(EventField.ip, fraudModel.getIp());
            case EMAIL:
                return new FieldModel(EventField.email, fraudModel.getEmail());
            case BIN:
                return new FieldModel(EventField.bin, fraudModel.getBin());
            case FINGERPRINT:
                return new FieldModel(EventField.fingerprint, fraudModel.getFingerprint());
            case PARTY_ID:
                return new FieldModel(EventField.partyId, fraudModel.getPartyId());
            case SHOP_ID:
                return new FieldModel(EventField.shopId, fraudModel.getShopId());
            default:
                throw new UnknownFieldException();
        }
    }

    public EventField resolve(CheckedField field) {
        switch (field) {
            case IP:
                return EventField.ip;
            case EMAIL:
                return EventField.email;
            case BIN:
                return EventField.bin;
            case FINGERPRINT:
                return EventField.fingerprint;
            case PARTY_ID:
                return EventField.partyId;
            case SHOP_ID:
                return EventField.shopId;
            default:
                throw new UnknownFieldException();
        }
    }

    @Data
    @AllArgsConstructor
    public class FieldModel {
        private EventField name;
        private String value;
    }
}
