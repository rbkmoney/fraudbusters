package com.rbkmoney.fraudbusters.util;

import com.rbkmoney.damsel.domain.BankCard;
import com.rbkmoney.damsel.domain.ClientInfo;
import com.rbkmoney.damsel.domain.ContactInfo;
import com.rbkmoney.damsel.domain.Payer;

import java.util.Optional;

public class PayerFieldExtractor {

    public static Optional<ContactInfo> getContactInfo(Payer payer) {
        if (payer.isSetPaymentResource()) {
            return Optional.ofNullable(payer.getPaymentResource().getContactInfo());
        } else if (payer.isSetCustomer()) {
            return Optional.ofNullable(payer.getCustomer().getContactInfo());
        } else if (payer.isSetRecurrent()) {
            return Optional.ofNullable(payer.getRecurrent().getContactInfo());
        }
        return Optional.empty();
    }

    public static Optional<BankCard> getBankCard(Payer payer) {
        if (payer.isSetCustomer() && payer.getCustomer().getPaymentTool().isSetBankCard()) {
            return Optional.ofNullable(payer.getCustomer().getPaymentTool().getBankCard());
        } else if (payer.isSetPaymentResource() && payer.getPaymentResource().getResource().getPaymentTool().isSetBankCard()) {
            return Optional.ofNullable(payer.getPaymentResource().getResource().getPaymentTool().getBankCard());
        } else if (payer.isSetRecurrent() && payer.getRecurrent().getPaymentTool().isSetBankCard()) {
            return Optional.ofNullable(payer.getRecurrent().getPaymentTool().getBankCard());
        }
        return Optional.empty();
    }

    public static Optional<ClientInfo> getClientInfo(Payer payer) {
        if (payer.isSetPaymentResource()) {
            return Optional.ofNullable(payer.getPaymentResource().getResource().getClientInfo());
        }
        return Optional.empty();
    }

}
