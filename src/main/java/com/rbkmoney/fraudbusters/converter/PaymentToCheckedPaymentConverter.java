package com.rbkmoney.fraudbusters.converter;

import com.rbkmoney.damsel.domain.PaymentTool;
import com.rbkmoney.damsel.fraudbusters.*;
import com.rbkmoney.damsel.fraudbusters.Error;
import com.rbkmoney.fraudbusters.constant.PaymentToolType;
import com.rbkmoney.fraudbusters.domain.CheckedPayment;
import com.rbkmoney.fraudbusters.domain.TimeProperties;
import com.rbkmoney.fraudbusters.util.TimestampUtil;
import com.rbkmoney.geck.common.util.TBaseUtil;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import static com.rbkmoney.fraudbusters.constant.ClickhouseUtilsValue.UNKNOWN;

@Component
public class PaymentToCheckedPaymentConverter implements Converter<Payment, CheckedPayment> {

    @NonNull
    @Override
    public CheckedPayment convert(Payment payment) {
        CheckedPayment checkedPayment = new CheckedPayment();
        TimeProperties timeProperties = TimestampUtil.generateTimePropertiesByString(payment.getEventTime());
        checkedPayment.setTimestamp(timeProperties.getTimestamp());
        checkedPayment.setEventTimeHour(timeProperties.getEventTimeHour());
        checkedPayment.setEventTime(timeProperties.getEventTime());
        checkedPayment.setId(payment.getId());

        ClientInfo clientInfo = payment.getClientInfo();
        checkedPayment.setEmail(clientInfo.getEmail());
        checkedPayment.setIp(clientInfo.getIp());
        checkedPayment.setFingerprint(clientInfo.getFingerprint());

        PaymentTool paymentTool = payment.getPaymentTool();
        checkedPayment.setPaymentTool(TBaseUtil.unionFieldToEnum(paymentTool, PaymentToolType.class).name());
        checkedPayment.setBin(paymentTool.isSetBankCard() ? paymentTool.getBankCard().getBin() : UNKNOWN);
        checkedPayment.setMaskedPan(paymentTool.isSetBankCard() ? paymentTool.getBankCard().getLastDigits() : UNKNOWN);
        checkedPayment.setCardToken(paymentTool.isSetBankCard() ? paymentTool.getBankCard().getToken() : UNKNOWN);
        checkedPayment.setPaymentSystem(paymentTool.isSetBankCard() ? paymentTool.getBankCard()
                .getPaymentSystem()
                .name() : UNKNOWN);

        ProviderInfo providerInfo = payment.getProviderInfo();
        checkedPayment.setTerminal(providerInfo.getTerminalId());
        checkedPayment.setProviderId(providerInfo.getProviderId());
        checkedPayment.setBankCountry(providerInfo.getCountry());

        ReferenceInfo referenceInfo = payment.getReferenceInfo();
        MerchantInfo merchantInfo = payment.getReferenceInfo().getMerchantInfo();
        checkedPayment.setPartyId(referenceInfo.isSetMerchantInfo() ? merchantInfo.getPartyId() : UNKNOWN);
        checkedPayment.setShopId(referenceInfo.isSetMerchantInfo() ? merchantInfo.getShopId() : UNKNOWN);

        checkedPayment.setAmount(payment.getCost().getAmount());
        checkedPayment.setCurrency(payment.getCost().getCurrency().getSymbolicCode());

        checkedPayment.setPaymentStatus(payment.getStatus().name());

        Error error = payment.getError();
        checkedPayment.setErrorCode(error == null ? null : error.getErrorCode());
        checkedPayment.setErrorReason(error == null ? null : error.getErrorReason());

        checkedPayment.setPayerType(payment.isSetPayerType() ? payment.getPayerType().name() : UNKNOWN);
        checkedPayment.setTokenProvider(paymentTool.isSetBankCard() && paymentTool.getBankCard().isSetTokenProvider()
                ? paymentTool.getBankCard().getTokenProvider().name()
                : UNKNOWN);
        checkedPayment.setMobile(payment.isMobile());
        checkedPayment.setRecurrent(payment.isRecurrent());
        return checkedPayment;
    }

}
