package com.rbkmoney.fraudbusters.serde;

import com.rbkmoney.kafka.common.serialization.ThriftSerializer;
import com.rbkmoney.machinegun.eventsink.SinkEvent;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serializer;

import java.util.Map;

@Slf4j
public class SinkEventSerde implements Serde<SinkEvent> {

    @Override
    public void configure(Map<String, ?> map, boolean b) {

    }

    @Override
    public void close() {

    }

    @Override
    public Serializer<SinkEvent> serializer() {
        return new ThriftSerializer<>();
    }

    @Override
    public Deserializer<SinkEvent> deserializer() {
        return new SinkEventDeserializer();
    }
}
