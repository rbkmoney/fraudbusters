package com.rbkmoney.fraudbusters.converter;

import com.rbkmoney.damsel.domain.ClientInfo;
import com.rbkmoney.damsel.domain.Payer;
import com.rbkmoney.damsel.proxy_inspector.Context;
import com.rbkmoney.damsel.proxy_inspector.Party;
import com.rbkmoney.damsel.proxy_inspector.PaymentInfo;
import com.rbkmoney.fraudo.model.FraudModel;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;


@Component
public class ContextToFraudModelConverter implements Converter<Context, FraudModel> {

    @Override
    public FraudModel convert(Context context) {
        FraudModel fraudModel = new FraudModel();
        PaymentInfo payment = context.getPayment();
        Party party = payment.getParty();
        fraudModel.setPartyId(party.getPartyId());
        Payer payer = payment.getPayment().getPayer();
        fraudModel.setBin(payer.getCustomer().getPaymentTool().getBankCard().getBin());
        fraudModel.setEmail(payer.getCustomer().getContactInfo().getEmail());
        fraudModel.setShopId(payment.getShop().getId());
        fraudModel.setAmount(payment.getPayment().getCost().getAmount());
        ClientInfo clientInfo = getClientInfo(context);
        if (clientInfo != null) {
            fraudModel.setIp(clientInfo.getIpAddress());
            fraudModel.setFingerprint(clientInfo.getFingerprint());
        }
        return fraudModel;
    }

    public static ClientInfo getClientInfo(Context context) {
        Payer payer = context.getPayment().getPayment().getPayer();
        if (payer.isSetPaymentResource()) {
            return payer.getPaymentResource().getResource().getClientInfo();
        }
        return null;
    }
}
