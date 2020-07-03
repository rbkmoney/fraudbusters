package com.rbkmoney.fraudbusters.serde;

import com.rbkmoney.damsel.fraudbusters.FraudPayment;
import com.rbkmoney.kafka.common.serialization.AbstractThriftDeserializer;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FraudPaymentDeserializer extends AbstractThriftDeserializer<FraudPayment> {

    @Override
    public FraudPayment deserialize(String topic, byte[] data) {
        return deserialize(data, new FraudPayment());
    }

}