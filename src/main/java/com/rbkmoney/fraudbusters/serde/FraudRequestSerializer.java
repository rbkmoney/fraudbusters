package com.rbkmoney.fraudbusters.serde;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rbkmoney.fraudbusters.domain.FraudRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serializer;

import java.util.Map;

@Slf4j
public class FraudRequestSerializer implements Serializer<FraudRequest> {

    private final ObjectMapper om = new ObjectMapper();

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {

    }

    @Override
    public byte[] serialize(String topic, FraudRequest data) {
        byte[] retVal = null;
        try {
            retVal = om.writeValueAsString(data).getBytes();
        } catch (Exception e) {
            log.error("Error when serialize fraudRequest data: {} ", data, e);
        }
        return retVal;
    }

    @Override
    public void close() {

    }

}
