package com.rbkmoney.fraudbusters.fraud.payment.resolver;

import com.rbkmoney.fraudbusters.fraud.constant.DgraphEntity;
import com.rbkmoney.fraudbusters.fraud.constant.PaymentCheckedField;
import com.rbkmoney.fraudbusters.fraud.model.PaymentModel;
import org.springframework.stereotype.Service;

@Service

public class DgraphQueryConditionResolver {

    public String resolvePaymentFilterByDgraphEntity(DgraphEntity entity) {
        return switch (entity) {
            case TOKEN -> "cardToken @filter(%s)";
            case BIN -> "bin @filter(%s)";
            case EMAIL -> "contactEmail @filter(%s)";
            case FINGERPRINT -> "fingerprint @filter(%s)";
            case IP -> "operationIp @filter(%s)";
            case PARTY -> "party @filter(%s)";
            case SHOP -> "shop @filter(%s)";
            case COUNTRY -> "country @filter(%s)";
            case CURRENCY -> "currency @filter(%s)";
            case FRAUD_PAYMENT -> "fraudPayment @filter(%s)";
            case PAYMENT -> "sourcePayment @filter(%s)";
            default -> throw new UnsupportedOperationException(String.format("Unknown %s", entity));
        };
    }

    public String resolveConditionByPaymentCheckedField(PaymentCheckedField paymentCheckedField,
                                                         PaymentModel paymentModel) {
        return switch (paymentCheckedField) {
            case BIN -> String.format("eq(cardBin, \"%s\")", paymentModel.getBin());
            case EMAIL -> String.format("eq(userEmail, \"%s\")", paymentModel.getEmail());
            //TODO: look at country IP
            case IP, COUNTRY_IP -> String.format("eq(ipAddress, \"%s\")", paymentModel.getIp());
            case FINGERPRINT -> String.format("eq(fingerprintData, \"%s\")", paymentModel.getFingerprint());
            case CARD_TOKEN -> String.format("eq(tokenId, \"%s\")", paymentModel.getCardToken());
            case PARTY_ID -> String.format("eq(partyId, \"%s\")", paymentModel.getPartyId());
            case SHOP_ID -> String.format("eq(shopId, \"%s\")", paymentModel.getShopId());
            case PAN -> String.format("eq(maskedPan, \"%s\")", paymentModel.getPan());
            case COUNTRY_BANK -> String.format("eq(countryName, \"%s\")", paymentModel.getBinCountryCode());
            case CURRENCY -> String.format("eq(currencyCode, \"%s\")", paymentModel.getCurrency());
            case MOBILE -> String.format("eq(mobile, %s)", paymentModel.isMobile());
            case RECURRENT -> String.format("eq(recurrent, %s)", paymentModel.isRecurrent());
            default -> throw new UnsupportedOperationException(String.format("Unknown %s", paymentCheckedField));
        };
    }

}
