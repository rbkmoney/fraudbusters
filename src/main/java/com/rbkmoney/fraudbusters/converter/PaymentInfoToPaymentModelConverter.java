package com.rbkmoney.fraudbusters.converter;

import com.rbkmoney.damsel.fraudbusters.ClientInfo;
import com.rbkmoney.damsel.fraudbusters.PaymentInfo;
import com.rbkmoney.fraudbusters.fraud.model.PaymentModel;
import com.rbkmoney.fraudbusters.util.TimestampUtil;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

@Component
public class PaymentInfoToPaymentModelConverter implements Converter<PaymentInfo, PaymentModel> {

    @Override
    @NonNull
    public PaymentModel convert(PaymentInfo paymentInfo) {
        // TODO: add bin, pan, bin country code, mobile and recurrent
        PaymentModel paymentModel = new PaymentModel();
        if (paymentInfo.isSetClientInfo()) {
            ClientInfo clientInfo = paymentInfo.getClientInfo();
            paymentModel.setIp(clientInfo.getIp());
            paymentModel.setEmail(clientInfo.getEmail());
            paymentModel.setFingerprint(clientInfo.getFingerprint());
        }

        paymentModel.setAmount(paymentInfo.getAmount());
        paymentModel.setCurrency(paymentInfo.getCurrency());

        paymentModel.setCardToken(paymentInfo.getCardToken());
        paymentModel.setShopId(paymentInfo.getMerchantInfo().getShopId());
        paymentModel.setPartyId(paymentInfo.getMerchantInfo().getPartyId());
        paymentModel.setTimestamp(TimestampUtil.parseInstantFromString(paymentInfo.getEventTime()).toEpochMilli());

        return paymentModel;
    }

}
