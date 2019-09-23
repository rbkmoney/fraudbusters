package com.rbkmoney.fraudbusters.repository.setter;

import com.rbkmoney.fraudbusters.constant.MgEventSinkField;
import com.rbkmoney.fraudbusters.domain.MgEventSinkRow;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class MgEventSinkParametersGenerator {

    @NotNull
    public static Map<String, Object> generateParamsByFraudModel(MgEventSinkRow mgEventSinkRow) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put(MgEventSinkField.timestamp.name(), mgEventSinkRow.getTimestamp());
        parameters.put(MgEventSinkField.eventTime.name(), mgEventSinkRow.getEventTime());
        Optional.ofNullable(mgEventSinkRow.getPartyId())
                .ifPresent(v -> parameters.put(MgEventSinkField.ip.name(), v));
        Optional.ofNullable(mgEventSinkRow.getPartyId())
                .ifPresent(v -> parameters.put(MgEventSinkField.email.name(), v));
        Optional.ofNullable(mgEventSinkRow.getPartyId())
                .ifPresent(v -> parameters.put(MgEventSinkField.bin.name(), v));
        Optional.ofNullable(mgEventSinkRow.getPartyId())
                .ifPresent(v -> parameters.put(MgEventSinkField.fingerprint.name(), v));
        Optional.ofNullable(mgEventSinkRow.getPartyId())
                .ifPresent(v -> parameters.put(MgEventSinkField.shopId.name(), v));
        Optional.ofNullable(mgEventSinkRow.getPartyId())
                .ifPresent(v -> parameters.put(MgEventSinkField.partyId.name(), v));
        Optional.ofNullable(mgEventSinkRow.getResultStatus())
                .ifPresent(v -> parameters.put(MgEventSinkField.resultStatus.name(), v));
        Optional.ofNullable(mgEventSinkRow.getAmount())
                .ifPresent(v -> parameters.put(MgEventSinkField.amount.name(), v));
        Optional.ofNullable(mgEventSinkRow.getCountry())
                .ifPresent(v -> parameters.put(MgEventSinkField.country.name(), v));
        Optional.ofNullable(mgEventSinkRow.getBankCountry())
                .ifPresent(v -> parameters.put(MgEventSinkField.bankCountry.name(), v));
        Optional.ofNullable(mgEventSinkRow.getCurrency())
                .ifPresent(v -> parameters.put(MgEventSinkField.currency.name(), v));
        Optional.ofNullable(mgEventSinkRow.getInvoiceId())
                .ifPresent(v -> parameters.put(MgEventSinkField.invoiceId.name(), v));
        Optional.ofNullable(mgEventSinkRow.getMaskedPan())
                .ifPresent(v -> parameters.put(MgEventSinkField.maskedPan.name(), v));
        Optional.ofNullable(mgEventSinkRow.getMaskedPan())
                .ifPresent(v -> parameters.put(MgEventSinkField.bankName.name(), v));
        Optional.ofNullable(mgEventSinkRow.getCardToken())
                .ifPresent(v -> parameters.put(MgEventSinkField.cardToken.name(), v));
        Optional.ofNullable(mgEventSinkRow.getPaymentId())
                .ifPresent(v -> parameters.put(MgEventSinkField.paymentId.name(), v));
        Optional.ofNullable(mgEventSinkRow.getErrorCode())
                .ifPresent(v -> parameters.put(MgEventSinkField.errorCode.name(), v));
        Optional.ofNullable(mgEventSinkRow.getErrorMessage())
                .ifPresent(v -> parameters.put(MgEventSinkField.errorMessage.name(), v));
        return parameters;
    }

}
