package com.rbkmoney.fraudbusters.stream.aggregate.handler;

import com.rbkmoney.damsel.domain.*;
import com.rbkmoney.damsel.payment_processing.InvoiceChange;
import com.rbkmoney.damsel.payment_processing.InvoicePaymentChange;
import com.rbkmoney.damsel.payment_processing.InvoicePaymentChangePayload;
import com.rbkmoney.damsel.payment_processing.InvoicePaymentStarted;
import com.rbkmoney.fraudbusters.domain.MgEventSinkRow;
import com.rbkmoney.geck.common.util.TypeUtil;
import org.springframework.stereotype.Component;

import java.sql.Date;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Component
public class InvoicePaymentStartedHandlerImpl implements InvoiceChangeHandler {

    @Override
    public boolean filter(InvoiceChange invoiceChange) {
        return invoiceChange.isSetInvoicePaymentChange()
                && invoiceChange.getInvoicePaymentChange().getPayload().isSetInvoicePaymentStarted();
    }

    @Override
    public MgEventSinkRow handle(MgEventSinkRow mgEventSinkRow, InvoiceChange invoiceChange) {
        InvoicePaymentChange invoicePaymentChange = invoiceChange.getInvoicePaymentChange();
        mgEventSinkRow.setPaymentId(invoicePaymentChange.getId());
        InvoicePaymentChangePayload payload = invoicePaymentChange.getPayload();
        InvoicePaymentStarted invoicePaymentStarted = payload.getInvoicePaymentStarted();
        Payer payer = invoicePaymentStarted.getPayment().getPayer();
        InvoicePayment payment = invoicePaymentStarted.getPayment();
        LocalDateTime localDateTime = TypeUtil.stringToLocalDateTime(payment.getCreatedAt());
        long timestamp = localDateTime.toEpochSecond(ZoneOffset.UTC);
        mgEventSinkRow.setTimestamp(new Date(timestamp));
        Cash cost = payment.getCost();
        mgEventSinkRow.setAmount(cost.getAmount());
        mgEventSinkRow.setCurrency(cost.getCurrency().getSymbolicCode());
        if (payer.isSetPaymentResource()) {
            if (payment.getPayer().getPaymentResource().isSetResource()) {
                ClientInfo clientInfo = payment.getPayer().getPaymentResource().getResource().getClientInfo();
                mgEventSinkRow.setIp(clientInfo.getIpAddress());
                mgEventSinkRow.setFingerprint(clientInfo.getFingerprint());
                if (payment.getPayer().getPaymentResource().isSetResource()
                        && payment.getPayer().getPaymentResource().getResource().isSetPaymentTool()
                        && payment.getPayer().getPaymentResource().getResource().getPaymentTool().isSetBankCard()) {
                    BankCard bankCard = payment.getPayer().getPaymentResource().getResource().getPaymentTool().getBankCard();
                    mgEventSinkRow.setBankCountry(bankCard.getIssuerCountry().name());
                    mgEventSinkRow.setBin(bankCard.getBin());
                    mgEventSinkRow.setMaskedPan(bankCard.getMaskedPan());
                    mgEventSinkRow.setCardToken(bankCard.getToken());
                    mgEventSinkRow.setBankName(bankCard.getBankName());
                }
            }
            mgEventSinkRow.setEmail(payer.getPaymentResource().getContactInfo().getEmail());
        }
        return mgEventSinkRow;
    }
}