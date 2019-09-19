package com.rbkmoney.fraudbusters.serde;

import com.rbkmoney.fraudbusters.domain.MgEventSinkRow;
import com.rbkmoney.machinegun.eventsink.MachineEvent;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serializer;

import java.util.Map;

@Slf4j
public class MgEventSinkRowSerde implements Serde<MgEventSinkRow> {


    @Override
    public void configure(Map<String, ?> map, boolean b) {

    }

    @Override
    public void close() {

    }

    @Override
    public Serializer<MgEventSinkRow> serializer() {
        return new MgEventSinkRowSerializer();
    }

    @Override
    public Deserializer<MgEventSinkRow> deserializer() {
        return new MgEventSinkRowDeserializer();
    }
}
