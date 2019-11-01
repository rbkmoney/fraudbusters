package com.rbkmoney.fraudbusters.converter;

import com.rbkmoney.damsel.domain.Cash;
import com.rbkmoney.damsel.domain.Payer;
import com.rbkmoney.damsel.p2p_insp.Context;
import com.rbkmoney.damsel.p2p_insp.Transfer;
import com.rbkmoney.damsel.p2p_insp.TransferInfo;
import com.rbkmoney.fraudbusters.constant.ClickhouseUtilsValue;
import com.rbkmoney.fraudbusters.fraud.model.P2PModel;
import com.rbkmoney.fraudbusters.util.PayerFieldExtractor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;


@Component
public class P2PContextToP2PModelConverter implements Converter<Context, P2PModel> {

    @Override
    public P2PModel convert(Context context) {
        P2PModel model = new P2PModel();

        TransferInfo transferInfo = context.getInfo();
        Transfer transfer = transferInfo.getTransfer();
        Cash cost = transfer.getCost();
        model.setIdentityId(transfer.getIdentity().getId());

        Payer sender = transfer.getSender().getRaw().getPayer();

        PayerFieldExtractor.getContactInfo(sender)
                .ifPresent(contract ->
                        model.setEmail(contract.getEmail()
                        )
                );

        model.setReceiver(initPayer(transfer.getReceiver().getRaw().getPayer()));
        model.setSender(initPayer(sender));
        model.setAmount(cost.getAmount());
        model.setCurrency(cost.getCurrency().getSymbolicCode());

        PayerFieldExtractor.getClientInfo(sender).ifPresent(info -> {
            model.setIp(info.getIpAddress());
            model.setFingerprint(info.getFingerprint());
        });

        return model;
    }

    private com.rbkmoney.fraudbusters.fraud.model.Payer initPayer(Payer receiver) {
        com.rbkmoney.fraudbusters.fraud.model.Payer.PayerBuilder receiverBuilder = com.rbkmoney.fraudbusters.fraud.model.Payer.builder();

        PayerFieldExtractor.getBankCard(receiver)
                .ifPresent(bankCard -> receiverBuilder
                        .bin(bankCard.getBin())
                        .country(bankCard.isSetIssuerCountry() ? bankCard.getIssuerCountry().name() : ClickhouseUtilsValue.UNKNOWN)
                        .cardToken(bankCard.getToken())
                        .pan(bankCard.getMaskedPan())
                );
        return receiverBuilder.build();
    }

}