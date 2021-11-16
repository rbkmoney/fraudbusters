package com.rbkmoney.fraudbusters.converter;

import com.rbkmoney.damsel.domain.PaymentTool;
import com.rbkmoney.damsel.fraudbusters.*;
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
        dgraphChargeback.setCurrency(convertCurrency(chargeback));
        dgraphChargeback.setStatus(chargeback.getStatus().name());
        dgraphChargeback.setPayerType(chargeback.getPayerType() == null ? null : chargeback.getPayerType().name());
        dgraphChargeback.setParty(convertParty(chargeback));
        dgraphChargeback.setShop(convertShop(chargeback));
        dgraphChargeback.setCardToken(convertToken(chargeback));
        dgraphChargeback.setPayment(convertPayment(chargeback));

        ClientInfo clientInfo = chargeback.getClientInfo();
        if (clientInfo != null) {
            dgraphChargeback.setEmail(clientInfo.getEmail() == null ? null : convertEmail(chargeback));
            dgraphChargeback.setFingerprint(
                    clientInfo.getFingerprint() == null ? null : convertFingerprint(chargeback));
            dgraphChargeback.setOperationIp(clientInfo.getIp() == null ? null : convertIp(chargeback));
        }
        PaymentTool paymentTool = chargeback.getPaymentTool();
        dgraphChargeback.setBin(paymentTool.isSetBankCard() ? convertBin(chargeback) : null);
        return dgraphChargeback;
    }

    private DgraphCurrency convertCurrency(Chargeback chargeback) {
        DgraphCurrency currency = new DgraphCurrency();
        currency.setCurrencyCode(chargeback.getCost().getCurrency().getSymbolicCode());
        return currency;
    }

    private DgraphToken convertToken(Chargeback chargeback) {
        DgraphToken dgraphToken = new DgraphToken();
        PaymentTool paymentTool = chargeback.getPaymentTool();
        dgraphToken.setTokenId(paymentTool.isSetBankCard() ? paymentTool.getBankCard().getToken() : UNKNOWN);
        dgraphToken.setMaskedPan(paymentTool.isSetBankCard() ? paymentTool.getBankCard().getLastDigits() : UNKNOWN);
        dgraphToken.setLastActTime(chargeback.getEventTime());
        return dgraphToken;
    }

    private DgraphEmail convertEmail(Chargeback chargeback) {
        DgraphEmail dgraphEmail = new DgraphEmail();
        dgraphEmail.setUserEmail(chargeback.getClientInfo().getEmail());
        dgraphEmail.setLastActTime(chargeback.getEventTime());
        return dgraphEmail;
    }

    private DgraphFingerprint convertFingerprint(Chargeback chargeback) {
        DgraphFingerprint dgraphFingerprint = new DgraphFingerprint();
        dgraphFingerprint.setFingerprintData(chargeback.getClientInfo().getFingerprint());
        dgraphFingerprint.setLastActTime(chargeback.getEventTime());
        return dgraphFingerprint;
    }

    private DgraphParty convertParty(Chargeback chargeback) {
        DgraphParty party = new DgraphParty();
        ReferenceInfo referenceInfo = chargeback.getReferenceInfo();
        MerchantInfo merchantInfo = chargeback.getReferenceInfo().getMerchantInfo();
        party.setPartyId(referenceInfo.isSetMerchantInfo() ? merchantInfo.getPartyId() : UNKNOWN);
        return party;
    }

    private DgraphShop convertShop(Chargeback chargeback) {
        DgraphShop shop = new DgraphShop();
        ReferenceInfo referenceInfo = chargeback.getReferenceInfo();
        MerchantInfo merchantInfo = chargeback.getReferenceInfo().getMerchantInfo();
        shop.setShopId(referenceInfo.isSetMerchantInfo() ? merchantInfo.getShopId() : UNKNOWN);
        return shop;
    }

    private DgraphIp convertIp(Chargeback chargeback) {
        DgraphIp dgraphIp = new DgraphIp();
        dgraphIp.setIp(chargeback.getClientInfo().getIp());
        return dgraphIp;
    }

    private DgraphPayment convertPayment(Chargeback chargeback) {
        DgraphPayment dgraphPayment = new DgraphPayment();
        dgraphPayment.setPaymentId(chargeback.getPaymentId());
        return dgraphPayment;
    }

    private DgraphBin convertBin(Chargeback chargeback) {
        DgraphBin dgraphBin = new DgraphBin();
        PaymentTool paymentTool = chargeback.getPaymentTool();
        dgraphBin.setBin(paymentTool.getBankCard().getBin());
        return dgraphBin;
    }

}