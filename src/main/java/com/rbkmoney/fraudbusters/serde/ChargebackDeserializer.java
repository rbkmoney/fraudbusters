package com.rbkmoney.fraudbusters.serde;


import com.rbkmoney.damsel.fraudbusters.Chargeback;
import com.rbkmoney.kafka.common.serialization.AbstractThriftDeserializer;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ChargebackDeserializer extends AbstractThriftDeserializer<Chargeback> {

    @Override
    public Chargeback deserialize(String topic, byte[] data) {
        return deserialize(data, new Chargeback());
    }

}