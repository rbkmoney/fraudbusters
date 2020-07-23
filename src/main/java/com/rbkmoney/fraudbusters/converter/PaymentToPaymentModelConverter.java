package com.rbkmoney.fraudbusters.converter;

import com.rbkmoney.damsel.fraudbusters.Payment;
import com.rbkmoney.fraudbusters.fraud.model.PaymentModel;
import com.rbkmoney.fraudbusters.util.TimestampUtil;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

@Component
public class PaymentToPaymentModelConverter implements Converter<Payment, PaymentModel> {

    @Override
    @NonNull
    public PaymentModel convert(Payment payment) {
        PaymentModel paymentModel = new PaymentModel();
        paymentModel.setPartyId(payment.getReferenceInfo().getMerchantInfo().getPartyId());
        paymentModel.setShopId(payment.getReferenceInfo().getMerchantInfo().getShopId());
        paymentModel.setBin(payment.getPaymentTool().getBankCard().getBin());
        paymentModel.setBinCountryCode(payment.getPaymentTool().getBankCard().getIssuerCountry().name());
        paymentModel.setCardToken(payment.getPaymentTool().getBankCard().getToken());
        paymentModel.setPan(payment.getPaymentTool().getBankCard().getLastDigits());
        paymentModel.setTimestamp(TimestampUtil.parseInstantFromString(payment.getEventTime()).toEpochMilli());
        return paymentModel;
    }

}
