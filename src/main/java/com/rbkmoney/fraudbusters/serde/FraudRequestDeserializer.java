package com.rbkmoney.fraudbusters.serde;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rbkmoney.fraudbusters.domain.FraudRequest;
import com.rbkmoney.fraudo.model.FraudModel;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Deserializer;

import java.util.Map;

@Slf4j
public class FraudRequestDeserializer implements Deserializer<FraudRequest> {

    private final ObjectMapper om = new ObjectMapper();

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {

    }

    @Override
    public FraudRequest deserialize(String topic, byte[] data) {
        FraudRequest fraudRequest = null;
        try {
            fraudRequest = om.readValue(data, FraudRequest.class);
        } catch (Exception e) {
            log.error("Error when deserialize fraudRequest data: {} ", data, e);
        }
        return fraudRequest;
    }

    @Override
    public void close() {

    }

}
