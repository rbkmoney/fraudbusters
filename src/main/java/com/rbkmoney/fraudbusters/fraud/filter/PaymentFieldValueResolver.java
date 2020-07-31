package com.rbkmoney.fraudbusters.fraud.filter;

import com.rbkmoney.fraudbusters.domain.CheckedPayment;
import com.rbkmoney.fraudbusters.fraud.constant.PaymentCheckedField;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PaymentFieldValueResolver {

    public Optional<String> resolve(String fieldName, CheckedPayment checkedPayment) {
        PaymentCheckedField byValue = PaymentCheckedField.valueOf(fieldName);
        switch (byValue) {
            case IP:
                return Optional.of(checkedPayment.getIp());
            case BIN:
                return Optional.of(checkedPayment.getBin());
            case CARD_TOKEN:
                return Optional.of(checkedPayment.getCardToken());
            case PARTY_ID:
                return Optional.of(checkedPayment.getPartyId());
            case EMAIL:
                return Optional.of(checkedPayment.getEmail());
            case PAN:
                return Optional.of(checkedPayment.getMaskedPan());
            case FINGERPRINT:
                return Optional.of(checkedPayment.getFingerprint());
            case SHOP_ID:
                return Optional.of(checkedPayment.getShopId());
            case COUNTRY_BANK:
                return Optional.of(checkedPayment.getBankCountry());
            case CURRENCY:
                return Optional.of(checkedPayment.getCurrency());
            case COUNTRY_IP:
                return Optional.of(checkedPayment.getPaymentCountry());
            default:
                return Optional.empty();
        }
    }

}
