package com.rbkmoney.fraudbusters.serde;

import com.rbkmoney.fraudbusters.domain.FraudResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.Map;

@Slf4j
public class FraudoResultSerde implements Serde<FraudResult> {


    @Override
    public void configure(Map<String, ?> map, boolean b) {

    }

    @Override
    public void close() {

    }

    @Override
    public Serializer<FraudResult> serializer() {
        return new JsonSerializer<>();
    }

    @Override
    public Deserializer<FraudResult> deserializer() {
        return new FraudoResultDeserializer();
    }
}
