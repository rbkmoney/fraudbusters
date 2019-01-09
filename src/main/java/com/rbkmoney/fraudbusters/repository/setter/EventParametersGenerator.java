package com.rbkmoney.fraudbusters.repository.setter;

import com.rbkmoney.fraudbusters.domain.Event;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

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
        return parameters;
    }

}
