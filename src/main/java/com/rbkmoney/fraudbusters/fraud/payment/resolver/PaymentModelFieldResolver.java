package com.rbkmoney.fraudbusters.fraud.payment.resolver;

import com.rbkmoney.fraudbusters.fraud.constant.PaymentCheckedField;
import com.rbkmoney.fraudbusters.fraud.model.PaymentModel;
import com.rbkmoney.fraudo.exception.UnresolvableFieldException;
import com.rbkmoney.fraudo.resolver.FieldResolver;

public class PaymentModelFieldResolver implements FieldResolver<PaymentModel, PaymentCheckedField> {

    @Override
    public String resolveValue(String fieldName, PaymentModel paymentModel) {
        return switch (PaymentCheckedField.getByValue(fieldName)) {
            case BIN -> paymentModel.getBin();
            case IP -> paymentModel.getIp();
            case FINGERPRINT -> paymentModel.getFingerprint();
            case EMAIL -> paymentModel.getEmail();
            case COUNTRY_BANK -> paymentModel.getBinCountryCode();
            case CARD_TOKEN -> paymentModel.getCardToken();
            case PAN -> paymentModel.getPan();
            default -> throw new UnresolvableFieldException(fieldName);
        };
    }

    @Override
    public PaymentCheckedField resolveName(String fieldName) {
        return PaymentCheckedField.getByValue(fieldName);
    }

}
