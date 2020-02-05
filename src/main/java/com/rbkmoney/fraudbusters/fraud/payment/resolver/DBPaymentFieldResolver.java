package com.rbkmoney.fraudbusters.fraud.payment.resolver;

import com.rbkmoney.fraudbusters.constant.EventField;
import com.rbkmoney.fraudbusters.exception.UnknownFieldException;
import com.rbkmoney.fraudbusters.fraud.constant.PaymentCheckedField;
import com.rbkmoney.fraudbusters.fraud.model.PaymentModel;
import com.rbkmoney.fraudbusters.fraud.model.FieldModel;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DBPaymentFieldResolver {

    @NotNull
    public List<FieldModel> resolveListFields(PaymentModel model, List<PaymentCheckedField> list) {
        if (list != null) {
            return list.stream()
                    .map(field -> resolve(field, model))
                    .collect(Collectors.toList());
        }
        return new ArrayList<>();
    }

    public FieldModel resolve(PaymentCheckedField field, PaymentModel model) {
        if (field == null) {
            throw new UnknownFieldException();
        }
        switch (field) {
            case IP:
                return new FieldModel(EventField.ip.name(), model.getIp());
            case EMAIL:
                return new FieldModel(EventField.email.name(), model.getEmail());
            case BIN:
                return new FieldModel(EventField.bin.name(), model.getBin());
            case FINGERPRINT:
                return new FieldModel(EventField.fingerprint.name(), model.getFingerprint());
            case PARTY_ID:
                return new FieldModel(EventField.partyId.name(), model.getPartyId());
            case SHOP_ID:
                return new FieldModel(EventField.shopId.name(), model.getShopId());
            case CARD_TOKEN:
                return new FieldModel(EventField.cardToken.name(), model.getCardToken());
            case PAN:
                return new FieldModel(EventField.maskedPan.name(), model.getPan());
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
