package com.rbkmoney.fraudbusters.serde;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rbkmoney.fraudo.model.FraudModel;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Deserializer;

import java.util.Map;

@Slf4j
public class FraudoModelDeserializer implements Deserializer<FraudModel> {

    private final ObjectMapper om = new ObjectMapper();

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {

    }

    @Override
    public FraudModel deserialize(String topic, byte[] data) {
        FraudModel user = null;
        try {
            user = om.readValue(data, FraudModel.class);
        } catch (Exception e) {
            log.error("Error when deserialize fraudModel data: {} e: ", data, e);
        }
        return user;
    }

    @Override
    public void close() {

    }

}
