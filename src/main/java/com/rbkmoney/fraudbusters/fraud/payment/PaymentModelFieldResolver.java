package com.rbkmoney.fraudbusters.fraud.payment;

import com.rbkmoney.fraudbusters.fraud.constant.PaymentCheckedField;
import com.rbkmoney.fraudbusters.fraud.model.PaymentModel;
import com.rbkmoney.fraudo.exception.UnresolvableFieldException;
import com.rbkmoney.fraudo.resolver.FieldResolver;

public class PaymentModelFieldResolver implements FieldResolver<PaymentModel, PaymentCheckedField> {

    @Override
    public String resolveValue(String fieldName, PaymentModel paymentModel) {
        switch (PaymentCheckedField.getByValue(fieldName)) {
            case BIN:
                return paymentModel.getBin();
            case IP:
                return paymentModel.getIp();
            case FINGERPRINT:
                return paymentModel.getFingerprint();
            case EMAIL:
                return paymentModel.getEmail();
            case COUNTRY_BANK:
                return paymentModel.getBinCountryCode();
            case CARD_TOKEN:
                return paymentModel.getCardToken();
            case PAN:
                return paymentModel.getPan();
            default:
                throw new UnresolvableFieldException(fieldName);
        }
    }

    @Override
    public PaymentCheckedField resolveName(String fieldName) {
        return PaymentCheckedField.getByValue(fieldName);
    }

}
