package com.rbkmoney.fraudbusters.converter;

import com.rbkmoney.damsel.domain.BankCard;
import com.rbkmoney.damsel.domain.PaymentTool;
import com.rbkmoney.damsel.fraudbusters.Chargeback;
import com.rbkmoney.damsel.fraudbusters.ClientInfo;
import com.rbkmoney.damsel.fraudbusters.MerchantInfo;
import com.rbkmoney.damsel.fraudbusters.ReferenceInfo;
import com.rbkmoney.fraudbusters.domain.dgraph.*;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import static com.rbkmoney.fraudbusters.constant.ClickhouseUtilsValue.UNKNOWN;

@Component
@RequiredArgsConstructor
public class ChargebackToDgraphChargebackConverter implements Converter<Chargeback, DgraphChargeback> {

    @Override
    public DgraphChargeback convert(Chargeback chargeback) {
        DgraphChargeback dgraphChargeback = new DgraphChargeback();
        dgraphChargeback.setChargebackId(chargeback.getId());
        dgraphChargeback.setPaymentId(chargeback.getPaymentId());
        dgraphChargeback.setCreatedAt(chargeback.getEventTime());
        dgraphChargeback.setAmount(chargeback.getCost().getAmount());
        dgraphChargeback.setStatus(chargeback.getStatus().name());
        dgraphChargeback.setPayerType(chargeback.getPayerType() == null ? null : chargeback.getPayerType().name());
        dgraphChargeback.setCurrency(new DgraphCurrency(chargeback.getCost().getCurrency().getSymbolicCode()));
        dgraphChargeback.setPayment(new DgraphPayment(chargeback.getPaymentId()));

        fillClientInfo(chargeback, dgraphChargeback);
        fillBankCardInfo(chargeback, dgraphChargeback);
        fillMerchantInfo(chargeback, dgraphChargeback);
        return dgraphChargeback;
    }

    private void fillBankCardInfo(Chargeback chargeback, DgraphChargeback dgraphChargeback) {
        PaymentTool paymentTool = chargeback.getPaymentTool();
        String createdAt = chargeback.getEventTime();
        if (paymentTool.isSetBankCard()) {
            BankCard bankCard = paymentTool.getBankCard();
            dgraphChargeback.setCardToken(new DgraphToken(bankCard.getToken(), bankCard.getLastDigits(), createdAt));
            dgraphChargeback.setBin(new DgraphBin(bankCard.getBin()));
        } else {
            dgraphChargeback.setCardToken(new DgraphToken(UNKNOWN, UNKNOWN, createdAt));
        }
    }

    private void fillClientInfo(Chargeback chargeback, DgraphChargeback dgraphChargeback) {
        ClientInfo clientInfo = chargeback.getClientInfo();
        if (clientInfo != null) {
            String createdAt = chargeback.getEventTime();
            dgraphChargeback.setFingerprint(clientInfo.getFingerprint() == null
                    ? null : new DgraphFingerprint(clientInfo.getFingerprint(), createdAt));
            dgraphChargeback.setEmail(clientInfo.getEmail() == null
                    ? null : new DgraphEmail(clientInfo.getEmail(), createdAt));
            dgraphChargeback.setOperationIp(clientInfo.getIp() == null
                    ? null : new DgraphIp(clientInfo.getIp(), createdAt));
        }
    }

    private void fillMerchantInfo(Chargeback chargeback, DgraphChargeback dgraphChargeback) {
        ReferenceInfo referenceInfo = chargeback.getReferenceInfo();
        MerchantInfo merchantInfo = chargeback.getReferenceInfo().getMerchantInfo();
        if (referenceInfo.isSetMerchantInfo()) {
            String createdAt = chargeback.getEventTime();
            dgraphChargeback.setParty(new DgraphParty(merchantInfo.getPartyId(), createdAt));
            dgraphChargeback.setShop(new DgraphShop(merchantInfo.getShopId(), createdAt));
        }
    }

}
