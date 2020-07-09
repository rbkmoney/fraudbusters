package com.rbkmoney.fraudbusters.converter;

import com.rbkmoney.damsel.domain.Payer;
import com.rbkmoney.damsel.proxy_inspector.Context;
import com.rbkmoney.damsel.proxy_inspector.Party;
import com.rbkmoney.damsel.proxy_inspector.PaymentInfo;
import com.rbkmoney.fraudbusters.constant.ClickhouseUtilsValue;
import com.rbkmoney.fraudbusters.constant.PayerType;
import com.rbkmoney.fraudbusters.domain.FraudRequest;
import com.rbkmoney.fraudbusters.domain.Metadata;
import com.rbkmoney.fraudbusters.fraud.model.PaymentModel;
import com.rbkmoney.fraudbusters.util.PayerFieldExtractor;
import com.rbkmoney.geck.common.util.TypeUtil;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneOffset;


@Component
public class ContextToFraudRequestConverter implements Converter<Context, FraudRequest> {

    @Override
    public FraudRequest convert(Context context) {
        PaymentModel paymentModel = new PaymentModel();
        PaymentInfo payment = context.getPayment();
        Party party = payment.getParty();
        paymentModel.setPartyId(party.getPartyId());
        Payer payer = context.getPayment().getPayment().getPayer();

        PayerFieldExtractor.getBankCard(payer)
                .ifPresent(bankCard -> {
            paymentModel.setBin(bankCard.getBin());
            paymentModel.setBinCountryCode(bankCard.isSetIssuerCountry() ? bankCard.getIssuerCountry().name() : ClickhouseUtilsValue.UNKNOWN);
            paymentModel.setCardToken(bankCard.getToken());
        });

        PayerFieldExtractor.getContactInfo(payer)
                .ifPresent(contract ->
                        paymentModel.setEmail(contract.getEmail())
                );

        paymentModel.setShopId(payment.getShop().getId());
        paymentModel.setAmount(payment.getPayment().getCost().getAmount());

        PayerFieldExtractor.getClientInfo(payer).ifPresent(info -> {
            paymentModel.setIp(info.getIpAddress());
            paymentModel.setFingerprint(info.getFingerprint());
        });
        FraudRequest fraudRequest = new FraudRequest();
        fraudRequest.setFraudModel(paymentModel);
        Metadata metadata = initMetadata(context);
        fraudRequest.setMetadata(metadata);
        return fraudRequest;
    }

    @NotNull
    private Metadata initMetadata(Context context) {
        Metadata metadata = new Metadata();
        PaymentInfo payment = context.getPayment();
        LocalDateTime localDateTime = TypeUtil.stringToLocalDateTime(payment.getPayment().getCreatedAt());
        metadata.setTimestamp(localDateTime.toEpochSecond(ZoneOffset.UTC));
        metadata.setCurrency(payment.getPayment().getCost().getCurrency().symbolic_code);
        metadata.setInvoiceId(payment.getInvoice().getId());
        metadata.setPaymentId(payment.getPayment().getId());
        PayerFieldExtractor.getBankCard(context.getPayment().getPayment().getPayer()).ifPresent(bankCard -> {
            metadata.setMaskedPan(bankCard.getLastDigits());
            metadata.setBankName(bankCard.getBankName());
            metadata.setPayerType(getPayerType(context.getPayment().getPayment().getPayer()));
            metadata.setTokenProvider(bankCard.isSetTokenProvider() ? bankCard.getTokenProvider().name() : ClickhouseUtilsValue.UNKNOWN);
        });
        return metadata;
    }

    @NotNull
    private String getPayerType(Payer payer) {
        if (payer.isSetPaymentResource()) {
            return PayerType.PAYMENT_RESOURCE.name();
        } else if (payer.isSetRecurrent()) {
            return PayerType.RECURRENT.name();
        } else if (payer.isSetCustomer()) {
            return PayerType.CUSTOMER.name();
        } else {
            return ClickhouseUtilsValue.UNKNOWN;
        }
    }

}
