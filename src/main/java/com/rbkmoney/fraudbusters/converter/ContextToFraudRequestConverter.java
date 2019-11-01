package com.rbkmoney.fraudbusters.converter;

import com.rbkmoney.damsel.domain.Payer;
import com.rbkmoney.damsel.proxy_inspector.Context;
import com.rbkmoney.damsel.proxy_inspector.Party;
import com.rbkmoney.damsel.proxy_inspector.PaymentInfo;
import com.rbkmoney.fraudbusters.constant.ClickhouseUtilsValue;
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
        PaymentModel fraudModel = new PaymentModel();
        PaymentInfo payment = context.getPayment();
        Party party = payment.getParty();
        fraudModel.setPartyId(party.getPartyId());
        Payer payer = context.getPayment().getPayment().getPayer();

        PayerFieldExtractor.getBankCard(payer)
                .ifPresent(bankCard -> {
            fraudModel.setBin(bankCard.getBin());
            fraudModel.setBinCountryCode(bankCard.isSetIssuerCountry() ? bankCard.getIssuerCountry().name() : ClickhouseUtilsValue.UNKNOWN);
            fraudModel.setCardToken(bankCard.getToken());
        });

        PayerFieldExtractor.getContactInfo(payer)
                .ifPresent(contract ->
                        fraudModel.setEmail(contract.getEmail())
                );

        fraudModel.setShopId(payment.getShop().getId());
        fraudModel.setAmount(payment.getPayment().getCost().getAmount());

        PayerFieldExtractor.getClientInfo(payer).ifPresent(info -> {
            fraudModel.setIp(info.getIpAddress());
            fraudModel.setFingerprint(info.getFingerprint());
        });
        FraudRequest fraudRequest = new FraudRequest();
        fraudRequest.setPaymentModel(fraudModel);
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
            metadata.setMaskedPan(bankCard.getMaskedPan());
            metadata.setBankName(bankCard.getBankName());
        });
        return metadata;
    }

}
