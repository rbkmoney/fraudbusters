package com.rbkmoney.fraudbusters.repository.setter;

import com.rbkmoney.fraudbusters.domain.Event;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class EventParametersGenerator {

    @NotNull
    public static Map<String, Object> generateParamsByFraudModel(Event value) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("timestamp", value.getTimestamp());
        parameters.put("ip", value.getIp());
        parameters.put("email", value.getEmail());
        parameters.put("bin", value.getBin());
        parameters.put("fingerprint", value.getFingerprint());
        parameters.put("shopId", value.getShopId());
        parameters.put("partyId", value.getPartyId());
        parameters.put("resultStatus", value.getResultStatus());
        parameters.put("amount", value.getAmount());
        parameters.put("eventTime", value.getEventTime());
        parameters.put("country", value.getCountry());
        parameters.put("checkedRule", value.getCheckedRule());
        parameters.put("bankCountry", value.getBankCountry());
        Optional.ofNullable(value.getCurrency()).ifPresent(v -> parameters.put("currency", v));
        Optional.ofNullable(value.getInvoiceId()).ifPresent(v -> parameters.put("invoiceId", v));
        Optional.ofNullable(value.getMaskedPan()).ifPresent(v -> parameters.put("maskedPan", v));
        Optional.ofNullable(value.getMaskedPan()).ifPresent(v -> parameters.put("bankName", v));
        Optional.ofNullable(value.getCardToken()).ifPresent(v -> parameters.put("cardToken", v));
        Optional.ofNullable(value.getPaymentId()).ifPresent(v -> parameters.put("paymentId", v));
        return parameters;
    }

}
