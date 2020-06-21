package com.rbkmoney.fraudbusters.serde;


import com.rbkmoney.damsel.fraudbusters.Payment;
import com.rbkmoney.kafka.common.serialization.AbstractThriftDeserializer;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PaymentDeserializer extends AbstractThriftDeserializer<Payment> {

    @Override
    public Payment deserialize(String topic, byte[] data) {
        return deserialize(data, new Payment());
    }

}