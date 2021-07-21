package com.rbkmoney.fraudbusters.converter;

import com.rbkmoney.damsel.fraudbusters.Error;
import com.rbkmoney.damsel.fraudbusters.*;
import com.rbkmoney.fraudbusters.domain.CheckedPayment;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.ZoneId;

@Component
public class CheckedPaymentToPaymentInfoConverter implements Converter<CheckedPayment, PaymentInfo> {

    @Override
    public PaymentInfo convert(CheckedPayment checkedPayment) {
        return new PaymentInfo()
                .setId(checkedPayment.getId())
                .setEventTime(
                        Instant.ofEpochMilli(checkedPayment.getEventTime())
                                .atZone(ZoneId.of("UTC"))
                                .toLocalDateTime()
                                .toString())
                .setCardToken(checkedPayment.getCardToken())
                .setAmount(checkedPayment.getAmount())
                .setPaymentTool(checkedPayment.getPaymentTool())
                .setPaymentCountry(checkedPayment.getPaymentCountry())
                .setCurrency(checkedPayment.getCurrency())
                .setPaymentSystem(checkedPayment.getPaymentSystem())
                .setMerchantInfo(new MerchantInfo()
                        .setPartyId(checkedPayment.getPartyId())
                        .setShopId(checkedPayment.getShopId()))
                .setClientInfo(new ClientInfo()
                        .setFingerprint(checkedPayment.getFingerprint())
                        .setIp(checkedPayment.getIp())
                        .setEmail(checkedPayment.getEmail()))
                .setError(new Error()
                        .setErrorCode(checkedPayment.getErrorCode())
                        .setErrorReason(checkedPayment.getErrorReason()))
                .setStatus(PaymentStatus.valueOf(checkedPayment.getPaymentStatus()))
                .setProvider(new ProviderInfo()
                        .setProviderId(checkedPayment.getProviderId())
                        .setCountry(checkedPayment.getBankCountry())
                        .setTerminalId(checkedPayment.getTerminal()));
    }
}
