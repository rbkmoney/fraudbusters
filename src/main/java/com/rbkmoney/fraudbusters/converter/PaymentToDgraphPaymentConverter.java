package com.rbkmoney.fraudbusters.converter;

import com.rbkmoney.damsel.domain.BankCard;
import com.rbkmoney.damsel.domain.PaymentTool;
import com.rbkmoney.damsel.fraudbusters.Error;
import com.rbkmoney.damsel.fraudbusters.*;
import com.rbkmoney.fraudbusters.constant.PaymentToolType;
import com.rbkmoney.fraudbusters.domain.dgraph.common.DgraphPayment;
import com.rbkmoney.fraudbusters.domain.dgraph.side.*;
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
        String createdAt = payment.getEventTime();
        dgraphPayment.setCreatedAt(createdAt);
        dgraphPayment.setAmount(payment.getCost().getAmount());
        dgraphPayment.setCurrency(new DgraphCurrency(payment.getCost().getCurrency().getSymbolicCode()));
        dgraphPayment.setStatus(payment.getStatus().name());

        PaymentTool paymentTool = payment.getPaymentTool();
        dgraphPayment.setPaymentTool(TBaseUtil.unionFieldToEnum(paymentTool, PaymentToolType.class).name());

        ProviderInfo providerInfo = payment.getProviderInfo();
        dgraphPayment.setProviderId(providerInfo.getProviderId());
        dgraphPayment.setTerminal(providerInfo.getTerminalId());
        dgraphPayment.setPayerType(payment.isSetPayerType() ? payment.getPayerType().name() : UNKNOWN);
        dgraphPayment.setCountry(providerInfo.getCountry() == null
                ? null : new DgraphCountry(providerInfo.getCountry()));
        dgraphPayment.setTokenProvider(paymentTool.isSetBankCard()
                && paymentTypeByContextResolver.isMobile(paymentTool.getBankCard())
                ? TokenProviderUtil.getTokenProviderName(paymentTool.getBankCard())
                : UNKNOWN);
        dgraphPayment.setMobile(payment.isMobile());
        dgraphPayment.setRecurrent(payment.isRecurrent());

        fillErrorInfo(payment, dgraphPayment);
        fillBankCardInfo(payment, dgraphPayment);
        fillClientInfo(payment, dgraphPayment);
        fillMerchantInfo(payment, dgraphPayment);

        return dgraphPayment;
    }

    private void fillBankCardInfo(Payment payment, DgraphPayment dgraphPayment) {
        PaymentTool paymentTool = payment.getPaymentTool();
        String createdAt = payment.getEventTime();
        if (paymentTool.isSetBankCard()) {
            BankCard bankCard = paymentTool.getBankCard();
            dgraphPayment.setCardToken(new DgraphToken(bankCard.getToken(), bankCard.getLastDigits(), createdAt));
            dgraphPayment.setBin(new DgraphBin(bankCard.getBin()));
        } else {
            dgraphPayment.setCardToken(new DgraphToken(UNKNOWN, UNKNOWN, createdAt));
        }
    }

    private void fillClientInfo(Payment payment, DgraphPayment dgraphPayment) {
        ClientInfo clientInfo = payment.getClientInfo();
        if (clientInfo != null) {
            String createdAt = payment.getEventTime();
            dgraphPayment.setFingerprint(clientInfo.getFingerprint() == null
                    ? null : new DgraphFingerprint(clientInfo.getFingerprint(), createdAt));
            dgraphPayment.setContactEmail(clientInfo.getEmail() == null
                    ? null : new DgraphEmail(clientInfo.getEmail(), createdAt));
            dgraphPayment.setOperationIp(clientInfo.getIp() == null
                    ? null : new DgraphIp(clientInfo.getIp(), createdAt));
        }
    }

    private void fillMerchantInfo(Payment payment, DgraphPayment dgraphPayment) {
        ReferenceInfo referenceInfo = payment.getReferenceInfo();
        MerchantInfo merchantInfo = payment.getReferenceInfo().getMerchantInfo();
        if (referenceInfo.isSetMerchantInfo()) {
            String createdAt = payment.getEventTime();
            dgraphPayment.setParty(new DgraphParty(merchantInfo.getPartyId(), createdAt));
            dgraphPayment.setShop(new DgraphShop(merchantInfo.getShopId(), createdAt));
        }
    }

    private void fillErrorInfo(Payment payment, DgraphPayment dgraphPayment) {
        Error error = payment.getError();
        dgraphPayment.setErrorCode(error == null ? null : error.getErrorCode());
        dgraphPayment.setErrorReason(error == null ? null : error.getErrorReason());
    }

}
