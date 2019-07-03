package com.rbkmoney.fraudbusters.serde;


import com.rbkmoney.damsel.fraudbusters.Command;
import com.rbkmoney.kafka.common.serialization.AbstractThriftDeserializer;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CommandDeserializer extends AbstractThriftDeserializer<Command> {

    @Override
    public Command deserialize(String topic, byte[] data) {
        return deserialize(data, new Command());
    }

}