package com.rbkmoney.fraudbusters.converter;

import com.rbkmoney.damsel.p2p_insp.Context;
import com.rbkmoney.fraudbusters.domain.FraudRequest;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;


@Component
public class P2PContextToFraudRequestConverter implements Converter<Context, FraudRequest> {

    @Override
    public FraudRequest convert(Context context) {
//        PaymentModel fraudModel = new PaymentModel();
//        TransferInfo transferInfo = context.getInfo();
//        Transfer transfer = transferInfo.getTransfer();
//
//        fraudModel.setPartyId(transfer.getIdentity().getId());
//        getBankCard(context).ifPresent(bankCard -> {
//            fraudModel.setBin(bankCard.getBin());
//            fraudModel.setBinCountryCode(bankCard.isSetIssuerCountry() ? bankCard.getIssuerCountry().name() : ClickhouseUtilsValue.UNKNOWN);
//            fraudModel.setCardToken(bankCard.getToken());
//        });
//        getContactInfo(context).ifPresent(contract -> fraudModel.setEmail(contract.getEmail()));
//        fraudModel.setShopId(payment.getShop().getId());
//        fraudModel.setAmount(payment.getPayment().getCost().getAmount());
//
//        getClientInfo(context).ifPresent(info -> {
//            fraudModel.setIp(info.getIpAddress());
//            fraudModel.setFingerprint(info.getFingerprint());
//        });
//        FraudRequest fraudRequest = new FraudRequest();
//        fraudRequest.setPaymentModel(fraudModel);
//        Metadata metadata = initMetadata(context);
//        fraudRequest.setMetadata(metadata);
//        return fraudRequest;

        return null;
    }
}
