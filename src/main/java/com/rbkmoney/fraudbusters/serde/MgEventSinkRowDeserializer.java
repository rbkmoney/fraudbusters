package com.rbkmoney.fraudbusters.serde;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rbkmoney.fraudbusters.domain.MgEventSinkRow;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Deserializer;

import java.util.Map;

@Slf4j
public class MgEventSinkRowDeserializer implements Deserializer<MgEventSinkRow> {

    private final ObjectMapper om = new ObjectMapper();

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {

    }

    @Override
    public MgEventSinkRow deserialize(String topic, byte[] data) {
        MgEventSinkRow mgEventSinkRow = null;
        try {
            mgEventSinkRow = om.readValue(data, MgEventSinkRow.class);
        } catch (Exception e) {
            log.error("Error when deserialize MgEventSinkRow data: {} ", data, e);
        }
        return mgEventSinkRow;
    }

    @Override
    public void close() {

    }

}
