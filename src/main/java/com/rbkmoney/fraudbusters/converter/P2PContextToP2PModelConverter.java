package com.rbkmoney.fraudbusters.converter;

import com.rbkmoney.damsel.domain.Cash;
import com.rbkmoney.damsel.domain.Payer;
import com.rbkmoney.damsel.p2p_insp.Context;
import com.rbkmoney.damsel.p2p_insp.Transfer;
import com.rbkmoney.damsel.p2p_insp.TransferInfo;
import com.rbkmoney.fraudbusters.constant.ClickhouseUtilsValue;
import com.rbkmoney.fraudbusters.fraud.model.P2PModel;
import com.rbkmoney.fraudbusters.util.PayerFieldExtractor;
import com.rbkmoney.geck.common.util.TypeUtil;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneOffset;


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
        model.setTransferId(transfer.getId());

        LocalDateTime localDateTime = TypeUtil.stringToLocalDateTime(transfer.getCreatedAt());
        model.setTimestamp(localDateTime.toInstant(ZoneOffset.UTC).toEpochMilli());

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

    private com.rbkmoney.fraudbusters.fraud.model.Payer initPayer(Payer payer) {
        com.rbkmoney.fraudbusters.fraud.model.Payer payerModel = new com.rbkmoney.fraudbusters.fraud.model.Payer();

        PayerFieldExtractor.getBankCard(payer)
                .ifPresent(bankCard -> {
                            payerModel.setBin(bankCard.getBin());
                            payerModel.setBinCountryCode(bankCard.isSetIssuerCountry() ? bankCard.getIssuerCountry().name() : ClickhouseUtilsValue.UNKNOWN);
                            payerModel.setCardToken(bankCard.getToken());
                            payerModel.setPan(bankCard.getLastDigits());
                            payerModel.setBankName(bankCard.getBankName());
                        }
                );

        return payerModel;
    }

}
