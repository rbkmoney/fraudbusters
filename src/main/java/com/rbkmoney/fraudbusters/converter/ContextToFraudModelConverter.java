package com.rbkmoney.fraudbusters.converter;

import com.rbkmoney.damsel.proxy_inspector.Context;
import com.rbkmoney.fraudo.model.FraudModel;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;


@Component
public class ContextToFraudModelConverter implements Converter<Context, FraudModel> {

    @Override
    public FraudModel convert(Context context) {
        FraudModel fraudModel = new FraudModel();
        fraudModel.setPartyId(context.getPayment().getParty().getPartyId());
        fraudModel.setBin(context.getPayment().getPayment().getPayer().getCustomer().getPaymentTool()
                .getBankCard().getBin());
        fraudModel.setEmail(context.getPayment().getPayment().getPayer().getCustomer().getContactInfo().getEmail());
        fraudModel.setShopId(context.getPayment().getShop().getId());
        return fraudModel;
    }
}
