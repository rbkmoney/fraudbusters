package com.rbkmoney.fraudbusters.serde;

import com.rbkmoney.fraudo.model.FraudModel;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serializer;

import java.util.Map;

@Slf4j
public class FraudoModelSerde implements Serde<FraudModel> {


    @Override
    public void configure(Map<String, ?> map, boolean b) {

    }

    @Override
    public void close() {

    }

    @Override
    public Serializer<FraudModel> serializer() {
        return new FraudoModelSerializer();
    }

    @Override
    public Deserializer<FraudModel> deserializer() {
        return new FraudoModelDeserializer();
    }
}
