package com.rbkmoney.fraudbusters.converter;

import com.rbkmoney.fraudbusters.constant.ClickhouseUtilsValue;
import com.rbkmoney.fraudbusters.domain.FraudRequest;
import com.rbkmoney.fraudbusters.domain.Metadata;
import com.rbkmoney.fraudbusters.fraud.model.PaymentModel;
import com.rbkmoney.geck.common.util.TypeUtil;
import com.rbkmoney.swag.fraudbusters.model.*;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneOffset;


@Component
@RequiredArgsConstructor
public class PaymentInspectRequestToFraudRequestConverter implements Converter<PaymentInspectRequest, FraudRequest> {

    @Override
    public FraudRequest convert(PaymentInspectRequest paymentInspectRequest) {
        PaymentModel paymentModel = new PaymentModel();
        PaymentContext context = paymentInspectRequest.getContext();

        paymentModel.setPartyId(context.getMerchantInfo().getPartyId());
        paymentModel.setShopId(context.getMerchantInfo().getShopId());

        BankCard bankCard = context.getBankCard();
        paymentModel.setBin(bankCard.getBin());
        paymentModel.setBinCountryCode(bankCard.getBinCountryCode() != null ? bankCard.getBinCountryCode() : ClickhouseUtilsValue.UNKNOWN);
        paymentModel.setCardToken(bankCard.getCardToken());
        paymentModel.setRecurrent(context.getRecurrent());
        paymentModel.setMobile(context.getMobile());

        UserInfo payerInfo = paymentInspectRequest.getContext().getPayerInfo();
        if (payerInfo != null) {
            paymentModel.setEmail(payerInfo.getEmail());
            paymentModel.setIp(payerInfo.getIp());
            paymentModel.setFingerprint(payerInfo.getFingerprint());
        }

        CashInfo cashInfo = context.getCashInfo();
        paymentModel.setAmount(cashInfo.getAmount());
        paymentModel.setCurrency(cashInfo.getCurrency());

        FraudRequest fraudRequest = new FraudRequest();
        fraudRequest.setFraudModel(paymentModel);
        Metadata metadata = initMetadata(context);
        fraudRequest.setMetadata(metadata);
        return fraudRequest;
    }

    @NotNull
    private Metadata initMetadata(PaymentContext context) {
        Metadata metadata = new Metadata();

//        LocalDateTime localDateTime = TypeUtil.stringToLocalDateTime(context.getCreatedAt());
//        metadata.setTimestamp(localDateTime.toEpochSecond(ZoneOffset.UTC));
        CashInfo cashInfo = context.getCashInfo();

        metadata.setCurrency(cashInfo.getCurrency());
        metadata.setInvoiceId(context.getInvoiceId());
        metadata.setPaymentId(context.getPaymentId());
        BankCard bankCard = context.getBankCard();
        metadata.setMaskedPan(bankCard.getLastDigits());
        metadata.setBankName(bankCard.getBankName());
        metadata.setPayerType(context.getPayerType().name());
        metadata.setTokenProvider(context.getTokenMobileProvider() != null ? context.getTokenMobileProvider()
                : ClickhouseUtilsValue.UNKNOWN);
        return metadata;
    }

}
