package com.rbkmoney.fraudbusters.serde;

import com.rbkmoney.fraudbusters.domain.FraudRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serializer;

import java.util.Map;

@Slf4j
public class FraudRequestSerde implements Serde<FraudRequest> {


    @Override
    public void configure(Map<String, ?> map, boolean b) {

    }

    @Override
    public void close() {

    }

    @Override
    public Serializer<FraudRequest> serializer() {
        return new FraudRequestSerializer();
    }

    @Override
    public Deserializer<FraudRequest> deserializer() {
        return new FraudRequestDeserializer();
    }
}
