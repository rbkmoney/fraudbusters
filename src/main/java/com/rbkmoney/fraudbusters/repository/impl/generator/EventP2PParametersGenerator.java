package com.rbkmoney.fraudbusters.repository.impl.generator;

import com.rbkmoney.fraudbusters.constant.EventP2PField;
import com.rbkmoney.fraudbusters.domain.EventP2P;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class EventP2PParametersGenerator {

    @NotNull
    public static Map<String, Object> generateParamsByFraudModel(EventP2P value) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put(EventP2PField.timestamp.name(), value.getTimestamp());
        parameters.put(EventP2PField.eventTime.name(), value.getEventTime());
        parameters.put(EventP2PField.eventTimeHour.name(), value.getEventTimeHour());

        parameters.put(EventP2PField.identityId.name(), value.getIdentityId());
        parameters.put(EventP2PField.transferId.name(), value.getTransferId());

        parameters.put(EventP2PField.ip.name(), value.getIp());
        parameters.put(EventP2PField.email.name(), value.getEmail());
        parameters.put(EventP2PField.bin.name(), value.getBin());
        parameters.put(EventP2PField.fingerprint.name(), value.getFingerprint());

        parameters.put(EventP2PField.amount.name(), value.getAmount());
        parameters.put(EventP2PField.currency.name(), value.getCurrency());

        Optional.ofNullable(value.getBankCountry()).ifPresent(v -> parameters.put(EventP2PField.bankCountry.name(), v));
        Optional.ofNullable(value.getCountry()).ifPresent(v -> parameters.put(EventP2PField.country.name(), v));
        Optional.ofNullable(value.getMaskedPan()).ifPresent(v -> parameters.put(EventP2PField.maskedPan.name(), v));
        Optional.ofNullable(value.getBankName()).ifPresent(v -> parameters.put(EventP2PField.bankName.name(), v));
        Optional.ofNullable(value.getCardTokenFrom())
                .ifPresent(v -> parameters.put(EventP2PField.cardTokenFrom.name(), v));
        Optional.ofNullable(value.getCardTokenTo()).ifPresent(v -> parameters.put(EventP2PField.cardTokenTo.name(), v));

        parameters.put(EventP2PField.resultStatus.name(), value.getResultStatus());
        parameters.put(EventP2PField.checkedRule.name(), value.getCheckedRule());
        Optional.ofNullable(value.getCheckedTemplate())
                .ifPresent(v -> parameters.put(EventP2PField.checkedTemplate.name(), v));
        return parameters;
    }

}
