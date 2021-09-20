package com.rbkmoney.fraudbusters.fraud.filter;

import com.rbkmoney.fraudbusters.domain.CheckedPayment;
import com.rbkmoney.fraudbusters.fraud.constant.PaymentCheckedField;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PaymentFieldValueResolver {

    public Optional<String> resolve(String fieldName, CheckedPayment checkedPayment) {
        PaymentCheckedField byValue = PaymentCheckedField.valueOf(fieldName);
        return switch (byValue) {
            case IP -> Optional.of(checkedPayment.getIp());
            case BIN -> Optional.of(checkedPayment.getBin());
            case CARD_TOKEN -> Optional.of(checkedPayment.getCardToken());
            case PARTY_ID -> Optional.of(checkedPayment.getPartyId());
            case EMAIL -> Optional.of(checkedPayment.getEmail());
            case PAN -> Optional.of(checkedPayment.getMaskedPan());
            case FINGERPRINT -> Optional.of(checkedPayment.getFingerprint());
            case SHOP_ID -> Optional.of(checkedPayment.getShopId());
            case COUNTRY_BANK -> Optional.of(checkedPayment.getBankCountry());
            case CURRENCY -> Optional.of(checkedPayment.getCurrency());
            case COUNTRY_IP -> Optional.of(checkedPayment.getPaymentCountry());
            default -> Optional.empty();
        };
    }

}
