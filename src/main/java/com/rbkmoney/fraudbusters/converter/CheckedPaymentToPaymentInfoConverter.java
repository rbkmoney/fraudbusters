package com.rbkmoney.fraudbusters.converter;

import com.rbkmoney.damsel.domain.CurrencyRef;
import com.rbkmoney.damsel.fraudbusters.Error;
import com.rbkmoney.damsel.fraudbusters.*;
import com.rbkmoney.fraudbusters.domain.CheckedPayment;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class CheckedPaymentToPaymentInfoConverter implements Converter<CheckedPayment, PaymentInfo> {

    @Override
    public PaymentInfo convert(CheckedPayment checkedPayment) {
        PaymentInfo paymentInfo = new PaymentInfo();
        paymentInfo.setId(checkedPayment.getId());
        paymentInfo.setEventTime(checkedPayment.getEventTime().toString());
        paymentInfo.setCardToken(checkedPayment.getCardToken());
        paymentInfo.setAmount(checkedPayment.getAmount());
        paymentInfo.setPaymentTool(checkedPayment.getPaymentTool());
        paymentInfo.setPaymentCountry(checkedPayment.getPaymentCountry());
        paymentInfo.setCurrency(new CurrencyRef().setSymbolicCode(checkedPayment.getCurrency()));
        MerchantInfo merchantInfo = new MerchantInfo();
        merchantInfo.setPartyId(checkedPayment.getPartyId());
        merchantInfo.setShopId(checkedPayment.getShopId());
        paymentInfo.setMerchantInfo(merchantInfo);
        ClientInfo clientInfo = new ClientInfo();
        clientInfo.setFingerprint(checkedPayment.getFingerprint());
        clientInfo.setIp(checkedPayment.getIp());
        clientInfo.setEmail(checkedPayment.getEmail());
        paymentInfo.setClientInfo(clientInfo);
        Error error = new Error();
        error.setErrorCode(checkedPayment.getErrorCode());
        error.setErrorReason(checkedPayment.getErrorReason());
        paymentInfo.setError(error);
        paymentInfo.setStatus(PaymentStatus.valueOf(checkedPayment.getPaymentStatus()));
        ProviderInfo providerInfo = new ProviderInfo();
        providerInfo.setProviderId(checkedPayment.getProviderId());
        providerInfo.setCountry(checkedPayment.getBankCountry());
        providerInfo.setTerminalId(checkedPayment.getTerminal());
        // TODO payment system
        return paymentInfo;
    }
}
