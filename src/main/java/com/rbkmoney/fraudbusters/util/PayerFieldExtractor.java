package com.rbkmoney.fraudbusters.util;

import com.rbkmoney.damsel.domain.BankCard;
import com.rbkmoney.damsel.domain.ClientInfo;
import com.rbkmoney.damsel.domain.ContactInfo;
import com.rbkmoney.damsel.domain.Payer;
import com.rbkmoney.fraudbusters.constant.ClickhouseUtilsValue;
import com.rbkmoney.fraudbusters.constant.PayerType;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Optional;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
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
        } else if (payer.isSetPaymentResource()
                && payer.getPaymentResource().getResource().getPaymentTool().isSetBankCard()) {
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

    public static String getPayerType(Payer payer) {
        if (payer.isSetPaymentResource()) {
            return PayerType.PAYMENT_RESOURCE.name();
        } else if (payer.isSetRecurrent()) {
            return PayerType.RECURRENT.name();
        } else if (payer.isSetCustomer()) {
            return PayerType.CUSTOMER.name();
        } else {
            return ClickhouseUtilsValue.UNKNOWN;
        }
    }
}
