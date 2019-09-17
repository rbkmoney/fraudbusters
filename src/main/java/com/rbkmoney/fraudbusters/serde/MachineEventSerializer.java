package com.rbkmoney.fraudbusters.serde;

import com.rbkmoney.machinegun.eventsink.MachineEvent;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serializer;
import org.apache.thrift.TSerializer;
import org.apache.thrift.protocol.TBinaryProtocol;

import java.util.Map;

@Slf4j
public class MachineEventSerializer implements Serializer<MachineEvent> {

    ThreadLocal<TSerializer> tSerializerThreadLocal = ThreadLocal.withInitial(() -> new TSerializer(new TBinaryProtocol.Factory()));

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {

    }

    @Override
    public byte[] serialize(String topic, MachineEvent data) {
        byte[] retVal = null;
        try {
            retVal = tSerializerThreadLocal.get().serialize(data);
        } catch (Exception e) {
            log.error("Error when serialize RuleTemplate data: {} ", data, e);
        }
        return retVal;
    }

    @Override
    public void close() {

    }

}
