package com.rbkmoney.fraudbusters.fraud.payment.resolver;

import com.rbkmoney.fraudbusters.fraud.constant.DgraphEntity;
import com.rbkmoney.fraudbusters.fraud.constant.PaymentCheckedField;
import com.rbkmoney.fraudbusters.fraud.model.PaymentModel;
import org.springframework.stereotype.Service;

@Service
public class DgraphQueryConditionResolver {

    private static final String FILTER_POSTFIX = " @filter(%s)";

    public String resolvePaymentFilterByDgraphEntity(DgraphEntity entity) {
        String entityName = switch (entity) {
            case TOKEN -> "cardToken";
            case BIN -> "bin";
            case EMAIL -> "contactEmail";
            case FINGERPRINT -> "fingerprint";
            case IP -> "operationIp";
            case PARTY -> "party";
            case SHOP -> "shop";
            case COUNTRY -> "country";
            case CURRENCY -> "currency";
            case FRAUD_PAYMENT -> "fraudPayment";
            case PAYMENT -> "sourcePayment";
            default -> throw new UnsupportedOperationException(String.format("Unknown %s", entity));
        };
        return entityName + FILTER_POSTFIX;
    }

    public String resolveConditionByPaymentCheckedField(PaymentCheckedField paymentCheckedField,
                                                        PaymentModel paymentModel) {
        return switch (paymentCheckedField) {
            case BIN -> String.format("eq(cardBin, \"%s\")", paymentModel.getBin());
            case EMAIL -> String.format("eq(userEmail, \"%s\")", paymentModel.getEmail());
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
