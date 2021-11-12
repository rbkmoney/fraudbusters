package com.rbkmoney.fraudbusters.converter;

import com.rbkmoney.damsel.domain.PaymentTool;
import com.rbkmoney.damsel.fraudbusters.*;
import com.rbkmoney.damsel.fraudbusters.Error;
import com.rbkmoney.fraudbusters.constant.PaymentToolType;
import com.rbkmoney.fraudbusters.domain.dgraph.*;
import com.rbkmoney.fraudbusters.util.PaymentTypeByContextResolver;
import com.rbkmoney.geck.common.util.TBaseUtil;
import com.rbkmoney.mamsel.TokenProviderUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import static com.rbkmoney.fraudbusters.constant.ClickhouseUtilsValue.UNKNOWN;

@Component
@RequiredArgsConstructor
public class PaymentToDgraphPaymentConverter implements Converter<Payment, DgraphPayment> {

    private final PaymentTypeByContextResolver paymentTypeByContextResolver;

    @Override
    public DgraphPayment convert(Payment payment) {
        DgraphPayment dgraphPayment = new DgraphPayment();
        dgraphPayment.setPaymentId(payment.getId());

        ReferenceInfo referenceInfo = payment.getReferenceInfo();
        MerchantInfo merchantInfo = payment.getReferenceInfo().getMerchantInfo();
        dgraphPayment.setCreatedAt(payment.getEventTime());
        dgraphPayment.setAmount(payment.getCost().getAmount());
        dgraphPayment.setCurrency(payment.getCost().getCurrency().getSymbolicCode());
        dgraphPayment.setStatus(payment.getStatus().name());

        PaymentTool paymentTool = payment.getPaymentTool();
        dgraphPayment.setPaymentTool(TBaseUtil.unionFieldToEnum(paymentTool, PaymentToolType.class).name());

        ProviderInfo providerInfo = payment.getProviderInfo();
        dgraphPayment.setProviderId(providerInfo.getProviderId());
        dgraphPayment.setTerminal(providerInfo.getTerminalId());
        dgraphPayment.setBankCountry(providerInfo.getCountry());
        dgraphPayment.setPayerType(payment.isSetPayerType() ? payment.getPayerType().name() : UNKNOWN);
        dgraphPayment.setTokenProvider(paymentTool.isSetBankCard()
                && paymentTypeByContextResolver.isMobile(paymentTool.getBankCard())
                ? TokenProviderUtil.getTokenProviderName(paymentTool.getBankCard())
                : UNKNOWN);
        dgraphPayment.setMobile(payment.isMobile());
        dgraphPayment.setRecurrent(payment.isRecurrent());

        Error error = payment.getError();
        dgraphPayment.setErrorCode(error == null ? null : error.getErrorCode());
        dgraphPayment.setErrorReason(error == null ? null : error.getErrorReason());

        dgraphPayment.setCardToken(convertToken(payment));
        ClientInfo clientInfo = payment.getClientInfo();
        if (clientInfo != null) {
            dgraphPayment.setFingerprint(clientInfo.getFingerprint() == null ? null : convertFingerprint(payment));
            dgraphPayment.setContactEmail(clientInfo.getEmail() == null ? null : convertEmail(payment));
            dgraphPayment.setOperationIp(clientInfo.getIp() == null ? null : convertIp(payment));
        }

        dgraphPayment.setBin(paymentTool.isSetBankCard() ? convertBin(payment) : null);
        dgraphPayment.setParty(convertParty(payment));
        dgraphPayment.setShop(convertShop(payment));
        dgraphPayment.setCountry(providerInfo.getCountry() == null ? null : convertCountry(payment));
        return dgraphPayment;
    }

    private DgraphToken convertToken(Payment payment) {
        DgraphToken dgraphToken = new DgraphToken();
        PaymentTool paymentTool = payment.getPaymentTool();
        dgraphToken.setTokenId(paymentTool.isSetBankCard() ? paymentTool.getBankCard().getToken() : UNKNOWN);
        dgraphToken.setMaskedPan(paymentTool.isSetBankCard() ? paymentTool.getBankCard().getLastDigits() : UNKNOWN);
        dgraphToken.setLastActTime(payment.getEventTime());
        return dgraphToken;
    }

    private DgraphEmail convertEmail(Payment payment) {
        DgraphEmail dgraphEmail = new DgraphEmail();
        dgraphEmail.setUserEmail(payment.getClientInfo().getEmail());
        dgraphEmail.setLastActTime(payment.getEventTime());
        return dgraphEmail;
    }

    private DgraphFingerprint convertFingerprint(Payment payment) {
        DgraphFingerprint dgraphFingerprint = new DgraphFingerprint();
        dgraphFingerprint.setFingerprintData(payment.getClientInfo().getFingerprint());
        dgraphFingerprint.setLastActTime(payment.getEventTime());
        return dgraphFingerprint;
    }

    private DgraphBin convertBin(Payment payment) {
        DgraphBin dgraphBin = new DgraphBin();
        PaymentTool paymentTool = payment.getPaymentTool();
        dgraphBin.setBin(paymentTool.getBankCard().getBin());
        return dgraphBin;
    }

    private DgraphCountry convertCountry(Payment payment) {
        DgraphCountry dgraphCountry = new DgraphCountry();
        dgraphCountry.setCountryName(payment.getProviderInfo().getCountry());
        return dgraphCountry;
    }

    private DgraphParty convertParty(Payment payment) {
        DgraphParty party = new DgraphParty();
        ReferenceInfo referenceInfo = payment.getReferenceInfo();
        MerchantInfo merchantInfo = payment.getReferenceInfo().getMerchantInfo();
        party.setPartyId(referenceInfo.isSetMerchantInfo() ? merchantInfo.getPartyId() : UNKNOWN);
        return party;
    }

    private DgraphShop convertShop(Payment payment) {
        DgraphShop shop = new DgraphShop();
        ReferenceInfo referenceInfo = payment.getReferenceInfo();
        MerchantInfo merchantInfo = payment.getReferenceInfo().getMerchantInfo();
        shop.setShopId(referenceInfo.isSetMerchantInfo() ? merchantInfo.getShopId() : UNKNOWN);
        return shop;
    }

    private DgraphIp convertIp(Payment payment) {
        DgraphIp dgraphIp = new DgraphIp();
        dgraphIp.setIp(payment.getClientInfo().getIp());
        return dgraphIp;
    }

}
