package com.rbkmoney.fraudbusters.serde;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rbkmoney.fraudbusters.domain.ScoresResult;
import com.rbkmoney.fraudbusters.fraud.model.P2PModel;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Deserializer;

import java.util.Map;

@Slf4j
public class P2PResultDeserializer implements Deserializer<ScoresResult<P2PModel>> {

    private final ObjectMapper om = new ObjectMapper();

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {

    }

    @Override
    public ScoresResult<P2PModel> deserialize(String topic, byte[] data) {
        ScoresResult<P2PModel> result = null;
        try {
            result = om.readValue(data, new TypeReference<>() {
            });
        } catch (Exception e) {
            log.error("Error when deserialize ScoresResult data: {} ", data, e);
        }
        return result;
    }

    @Override
    public void close() {

    }

}
