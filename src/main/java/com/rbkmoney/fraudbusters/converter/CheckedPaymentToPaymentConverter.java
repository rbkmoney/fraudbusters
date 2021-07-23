package com.rbkmoney.fraudbusters.converter;

import com.rbkmoney.damsel.domain.*;
import com.rbkmoney.damsel.fraudbusters.ClientInfo;
import com.rbkmoney.damsel.fraudbusters.Error;
import com.rbkmoney.damsel.fraudbusters.*;
import com.rbkmoney.fraudbusters.domain.CheckedPayment;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.ZoneId;

@Component
public class CheckedPaymentToPaymentConverter implements Converter<CheckedPayment, Payment> {

    @Override
    public Payment convert(CheckedPayment checkedPayment) {
        ReferenceInfo referenceInfo = new ReferenceInfo();
        referenceInfo.setMerchantInfo(
                new MerchantInfo()
                        .setPartyId(checkedPayment.getPartyId())
                        .setShopId(checkedPayment.getShopId()));
        PaymentTool paymentTool = new PaymentTool();
        BankCard bankCard = new BankCard();
        paymentTool.setBankCard(bankCard);
        bankCard.setToken(checkedPayment.getCardToken());
        bankCard.setPaymentSystem(new PaymentSystemRef().setId(checkedPayment.getPaymentSystem()));
        bankCard.setIssuerCountry(CountryCode.valueOf(checkedPayment.getPaymentCountry())); //TODO верно?
        return new Payment()
                .setId(checkedPayment.getId())
                .setEventTime(
                        Instant.ofEpochMilli(checkedPayment.getEventTime())
                                .atZone(ZoneId.of("UTC"))
                                .toLocalDateTime()
                                .toString())
                .setClientInfo(new ClientInfo()
                        .setFingerprint(checkedPayment.getFingerprint())
                        .setIp(checkedPayment.getIp())
                        .setEmail(checkedPayment.getEmail()))
                .setReferenceInfo(referenceInfo)
                .setError(new Error()
                        .setErrorCode(checkedPayment.getErrorCode())
                        .setErrorReason(checkedPayment.getErrorReason()))
                .setCost(new Cash()
                        .setAmount(checkedPayment.getAmount())
                        .setCurrency(new CurrencyRef()
                                .setSymbolicCode(checkedPayment.getCurrency())))
                .setStatus(PaymentStatus.valueOf(checkedPayment.getPaymentStatus()))
                .setPaymentTool(paymentTool)
                .setProviderInfo(new ProviderInfo()
                        .setProviderId(checkedPayment.getProviderId())
                        .setCountry(checkedPayment.getBankCountry())
                        .setTerminalId(checkedPayment.getTerminal()));
    }
}
