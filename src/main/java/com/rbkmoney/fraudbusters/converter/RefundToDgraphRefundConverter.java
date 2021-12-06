package com.rbkmoney.fraudbusters.converter;

import com.rbkmoney.damsel.domain.BankCard;
import com.rbkmoney.damsel.domain.PaymentTool;
import com.rbkmoney.damsel.fraudbusters.ClientInfo;
import com.rbkmoney.damsel.fraudbusters.MerchantInfo;
import com.rbkmoney.damsel.fraudbusters.ReferenceInfo;
import com.rbkmoney.damsel.fraudbusters.Refund;
import com.rbkmoney.fraudbusters.domain.dgraph.common.DgraphPayment;
import com.rbkmoney.fraudbusters.domain.dgraph.common.DgraphRefund;
import com.rbkmoney.fraudbusters.domain.dgraph.side.*;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import static com.rbkmoney.fraudbusters.constant.ClickhouseUtilsValue.UNKNOWN;

@Component
@RequiredArgsConstructor
public class RefundToDgraphRefundConverter implements Converter<Refund, DgraphRefund> {

    @Override
    public DgraphRefund convert(Refund refund) {
        DgraphRefund dgraphRefund = new DgraphRefund();
        dgraphRefund.setRefundId(refund.getId());
        dgraphRefund.setPaymentId(refund.getPaymentId());
        final String createdAt = refund.getEventTime();
        dgraphRefund.setCreatedAt(createdAt);
        dgraphRefund.setAmount(refund.getCost().getAmount());
        dgraphRefund.setStatus(refund.getStatus().name());
        dgraphRefund.setPayerType(refund.getPayerType() == null ? null : refund.getPayerType().name());

        dgraphRefund.setCurrency(new DgraphCurrency(refund.getCost().getCurrency().getSymbolicCode()));
        dgraphRefund.setSourcePayment(new DgraphPayment(refund.getPaymentId()));

        fillMerchantInfo(refund, dgraphRefund);
        fillClientInfo(refund, dgraphRefund);
        fillBankCardInfo(refund, dgraphRefund);
        return dgraphRefund;
    }

    private void fillBankCardInfo(Refund refund, DgraphRefund dgraphRefund) {
        PaymentTool paymentTool = refund.getPaymentTool();
        String createdAt = refund.getEventTime();
        if (paymentTool.isSetBankCard()) {
            BankCard bankCard = paymentTool.getBankCard();
            dgraphRefund.setCardToken(new DgraphToken(bankCard.getToken(), bankCard.getLastDigits(), createdAt));
            dgraphRefund.setBin(new DgraphBin(bankCard.getBin()));
        } else {
            dgraphRefund.setCardToken(new DgraphToken(UNKNOWN, UNKNOWN, createdAt));
        }
    }

    private void fillClientInfo(Refund refund, DgraphRefund dgraphRefund) {
        ClientInfo clientInfo = refund.getClientInfo();
        if (clientInfo != null) {
            String createdAt = refund.getEventTime();
            dgraphRefund.setFingerprint(clientInfo.getFingerprint() == null
                    ? null : new DgraphFingerprint(clientInfo.getFingerprint(), createdAt));
            dgraphRefund.setContactEmail(clientInfo.getEmail() == null
                    ? null : new DgraphEmail(clientInfo.getEmail(), createdAt));
            dgraphRefund.setOperationIp(clientInfo.getIp() == null
                    ? null : new DgraphIp(clientInfo.getIp(), createdAt));
        }
    }

    private void fillMerchantInfo(Refund refund, DgraphRefund dgraphRefund) {
        ReferenceInfo referenceInfo = refund.getReferenceInfo();
        MerchantInfo merchantInfo = refund.getReferenceInfo().getMerchantInfo();
        if (referenceInfo.isSetMerchantInfo()) {
            String createdAt = refund.getEventTime();
            dgraphRefund.setParty(new DgraphParty(merchantInfo.getPartyId(), createdAt));
            dgraphRefund.setShop(new DgraphShop(merchantInfo.getShopId(), createdAt));
        }
    }

}
