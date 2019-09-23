package com.rbkmoney.fraudbusters.repository.setter;

import com.rbkmoney.fraudbusters.constant.EventField;
import com.rbkmoney.fraudbusters.domain.Event;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class EventParametersGenerator {

    @NotNull
    public static Map<String, Object> generateParamsByFraudModel(Event value) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put(EventField.timestamp.name(), value.getTimestamp());
        parameters.put(EventField.ip.name(), value.getIp());
        parameters.put(EventField.email.name(), value.getEmail());
        parameters.put(EventField.bin.name(), value.getBin());
        parameters.put(EventField.fingerprint.name(), value.getFingerprint());
        parameters.put(EventField.shopId.name(), value.getShopId());
        parameters.put(EventField.partyId.name(), value.getPartyId());
        parameters.put(EventField.resultStatus.name(), value.getResultStatus());
        parameters.put(EventField.amount.name(), value.getAmount());
        parameters.put(EventField.eventTime.name(), value.getEventTime());
        parameters.put(EventField.country.name(), value.getCountry());
        parameters.put(EventField.checkedRule.name(), value.getCheckedRule());
        parameters.put(EventField.bankCountry.name(), value.getBankCountry());
        Optional.ofNullable(value.getCurrency()).ifPresent(v -> parameters.put(EventField.currency.name(), v));
        Optional.ofNullable(value.getInvoiceId()).ifPresent(v -> parameters.put(EventField.invoiceId.name(), v));
        Optional.ofNullable(value.getMaskedPan()).ifPresent(v -> parameters.put(EventField.maskedPan.name(), v));
        Optional.ofNullable(value.getMaskedPan()).ifPresent(v -> parameters.put(EventField.bankName.name(), v));
        Optional.ofNullable(value.getCardToken()).ifPresent(v -> parameters.put(EventField.cardToken.name(), v));
        Optional.ofNullable(value.getPaymentId()).ifPresent(v -> parameters.put(EventField.paymentId.name(), v));
        Optional.ofNullable(value.getCheckedTemplate()).ifPresent(v -> parameters.put(EventField.checkedTemplate.name(), v));
        return parameters;
    }

}
