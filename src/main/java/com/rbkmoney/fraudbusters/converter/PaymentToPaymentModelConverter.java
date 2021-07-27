package com.rbkmoney.fraudbusters.converter;

import com.rbkmoney.damsel.fraudbusters.Payment;
import com.rbkmoney.fraudbusters.fraud.model.PaymentModel;
import com.rbkmoney.fraudbusters.util.TimestampUtil;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import static com.rbkmoney.fraudbusters.constant.ClickhouseUtilsValue.UNKNOWN;

@Component
public class PaymentToPaymentModelConverter implements Converter<Payment, PaymentModel> {

    @Override
    @NonNull
    public PaymentModel convert(Payment payment) {
        PaymentModel paymentModel = new PaymentModel();
        paymentModel.setPartyId(payment.getReferenceInfo().getMerchantInfo().getPartyId());
        paymentModel.setShopId(payment.getReferenceInfo().getMerchantInfo().getShopId());
        paymentModel.setBin(payment.getPaymentTool().getBankCard().getBin());
        paymentModel.setBinCountryCode(payment.getPaymentTool().getBankCard().isSetIssuerCountry()
                ? payment.getPaymentTool().getBankCard().getIssuerCountry().name()
                : UNKNOWN);
        paymentModel.setIp(payment.getClientInfo().getIp());
        paymentModel.setFingerprint(payment.getClientInfo().getFingerprint());
        paymentModel.setAmount(payment.getCost().getAmount());
        paymentModel.setCurrency(payment.getCost().getCurrency().getSymbolicCode());
        paymentModel.setMobile(payment.isMobile());
        paymentModel.setRecurrent(payment.isRecurrent());
        paymentModel.setCardToken(payment.getPaymentTool().getBankCard().getToken());
        paymentModel.setPan(payment.getPaymentTool().getBankCard().getLastDigits());
        paymentModel.setTimestamp(TimestampUtil.parseInstantFromString(payment.getEventTime()).toEpochMilli());
        return paymentModel;
    }

}
