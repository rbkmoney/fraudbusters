package com.rbkmoney.fraudbusters.fraud.payment.resolver;

import com.rbkmoney.fraudbusters.constant.EventField;
import com.rbkmoney.fraudbusters.exception.UnknownFieldException;
import com.rbkmoney.fraudbusters.fraud.constant.PaymentCheckedField;
import com.rbkmoney.fraudbusters.fraud.model.FieldModel;
import com.rbkmoney.fraudbusters.fraud.model.PaymentModel;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DatabasePaymentFieldResolver {

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
        return switch (field) {
            case IP, COUNTRY_IP -> new FieldModel(EventField.ip.name(), model.getIp());
            case EMAIL -> new FieldModel(EventField.email.name(), model.getEmail());
            case BIN -> new FieldModel(EventField.bin.name(), model.getBin());
            case FINGERPRINT -> new FieldModel(EventField.fingerprint.name(), model.getFingerprint());
            case PARTY_ID -> new FieldModel(EventField.partyId.name(), model.getPartyId());
            case SHOP_ID -> new FieldModel(EventField.shopId.name(), model.getShopId());
            case CARD_TOKEN -> new FieldModel(EventField.cardToken.name(), model.getCardToken());
            case PAN -> new FieldModel(EventField.maskedPan.name(), model.getPan());
            case MOBILE -> new FieldModel(EventField.mobile.name(), model.isMobile());
            case RECURRENT -> new FieldModel(EventField.recurrent.name(), model.isRecurrent());
            case CURRENCY -> new FieldModel(EventField.currency.name(), model.getCurrency());
            case COUNTRY_BANK -> new FieldModel(EventField.bankCountry.name(), model.getBinCountryCode());
            default -> throw new UnknownFieldException();
        };
    }

    public String resolve(PaymentCheckedField field) {
        if (field == null) {
            throw new UnknownFieldException();
        }
        return switch (field) {
            case IP -> EventField.ip.name();
            case EMAIL -> EventField.email.name();
            case BIN -> EventField.bin.name();
            case PAN -> EventField.maskedPan.name();
            case FINGERPRINT -> EventField.fingerprint.name();
            case PARTY_ID -> EventField.partyId.name();
            case SHOP_ID -> EventField.shopId.name();
            case CARD_TOKEN -> EventField.cardToken.name();
            case MOBILE -> EventField.mobile.name();
            case RECURRENT -> EventField.recurrent.name();
            default -> throw new UnknownFieldException();
        };
    }

}
