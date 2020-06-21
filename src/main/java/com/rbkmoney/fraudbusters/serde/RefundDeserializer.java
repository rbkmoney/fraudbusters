package com.rbkmoney.fraudbusters.serde;


import com.rbkmoney.damsel.fraudbusters.Refund;
import com.rbkmoney.kafka.common.serialization.AbstractThriftDeserializer;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RefundDeserializer extends AbstractThriftDeserializer<Refund> {

    @Override
    public Refund deserialize(String topic, byte[] data) {
        return deserialize(data, new Refund());
    }

}