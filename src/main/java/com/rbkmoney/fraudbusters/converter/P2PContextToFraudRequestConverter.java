package com.rbkmoney.fraudbusters.converter;

import com.rbkmoney.damsel.domain.BankCard;
import com.rbkmoney.damsel.domain.ClientInfo;
import com.rbkmoney.damsel.domain.ContactInfo;
import com.rbkmoney.damsel.domain.Payer;
import com.rbkmoney.damsel.p2p_insp.Context;
import com.rbkmoney.damsel.p2p_insp.Transfer;
import com.rbkmoney.damsel.p2p_insp.TransferInfo;
import com.rbkmoney.damsel.proxy_inspector.Party;
import com.rbkmoney.damsel.proxy_inspector.PaymentInfo;
import com.rbkmoney.fraudbusters.constant.ClickhouseUtilsValue;
import com.rbkmoney.fraudbusters.domain.FraudRequest;
import com.rbkmoney.fraudbusters.domain.Metadata;
import com.rbkmoney.fraudo.model.FraudModel;
import com.rbkmoney.geck.common.util.TypeUtil;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Optional;


@Component
public class P2PContextToFraudRequestConverter implements Converter<Context, FraudRequest> {

    @Override
    public FraudRequest convert(Context context) {
        FraudModel fraudModel = new FraudModel();
        TransferInfo transferInfo = context.getInfo();
        Transfer transfer = transferInfo.getTransfer();

        fraudModel.setPartyId(transfer.getIdentity().getId());
        getBankCard(context).ifPresent(bankCard -> {
            fraudModel.setBin(bankCard.getBin());
            fraudModel.setBinCountryCode(bankCard.isSetIssuerCountry() ? bankCard.getIssuerCountry().name() : ClickhouseUtilsValue.UNKNOWN);
            fraudModel.setCardToken(bankCard.getToken());
        });
        getContactInfo(context).ifPresent(contract -> fraudModel.setEmail(contract.getEmail()));
        fraudModel.setShopId(payment.getShop().getId());
        fraudModel.setAmount(payment.getPayment().getCost().getAmount());

        getClientInfo(context).ifPresent(info -> {
            fraudModel.setIp(info.getIpAddress());
            fraudModel.setFingerprint(info.getFingerprint());
        });
        FraudRequest fraudRequest = new FraudRequest();
        fraudRequest.setFraudModel(fraudModel);
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
        getBankCard(context).ifPresent(bankCard -> {
            metadata.setMaskedPan(bankCard.getMaskedPan());
            metadata.setBankName(bankCard.getBankName());
        });
        return metadata;
    }

    private Optional<ClientInfo> getClientInfo(Context context) {
        Payer payer = context.getPayment().getPayment().getPayer();
        if (payer.isSetPaymentResource()) {
            return Optional.ofNullable(payer.getPaymentResource().getResource().getClientInfo());
        }
        return Optional.empty();
    }

    private Optional<BankCard> getBankCard(Context context) {
        Payer payer = context.getPayment().getPayment().getPayer();
        if (payer.isSetCustomer() && payer.getCustomer().getPaymentTool().isSetBankCard()) {
            return Optional.ofNullable(payer.getCustomer().getPaymentTool().getBankCard());
        } else if (payer.isSetPaymentResource() && payer.getPaymentResource().getResource().getPaymentTool().isSetBankCard()) {
            return Optional.ofNullable(payer.getPaymentResource().getResource().getPaymentTool().getBankCard());
        } else if (payer.isSetRecurrent() && payer.getRecurrent().getPaymentTool().isSetBankCard()) {
            return Optional.ofNullable(payer.getRecurrent().getPaymentTool().getBankCard());
        }
        return Optional.empty();
    }

    private Optional<ContactInfo> getContactInfo(Context context) {
        Payer payer = context.getPayment().getPayment().getPayer();
        if (payer.isSetPaymentResource()) {
            return Optional.ofNullable(payer.getPaymentResource().getContactInfo());
        } else if (payer.isSetCustomer()) {
            return Optional.ofNullable(payer.getCustomer().getContactInfo());
        } else if (payer.isSetRecurrent()) {
            return Optional.ofNullable(payer.getRecurrent().getContactInfo());
        }
        return Optional.empty();
    }
}
