package com.rbkmoney.fraudbusters.serde;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rbkmoney.fraudbusters.domain.FraudResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Deserializer;

import java.util.Map;

@Slf4j
public class FraudoResultDeserializer implements Deserializer<FraudResult> {

    private final ObjectMapper om = new ObjectMapper();

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {

    }

    @Override
    public FraudResult deserialize(String topic, byte[] data) {
        FraudResult fraudResult = null;
        try {
            fraudResult = om.readValue(data, FraudResult.class);
        } catch (Exception e) {
            log.error("Error when deserialize FraudResult data: {} ", data, e);
        }
        return fraudResult;
    }

    @Override
    public void close() {

    }

}
