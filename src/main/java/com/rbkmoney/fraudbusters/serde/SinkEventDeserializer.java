package com.rbkmoney.fraudbusters.serde;

import com.rbkmoney.fraudbusters.config.service.ListenersConfigurationService;
import com.rbkmoney.kafka.common.serialization.AbstractThriftDeserializer;
import com.rbkmoney.machinegun.eventsink.SinkEvent;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SinkEventDeserializer extends AbstractThriftDeserializer<SinkEvent> {

    @SneakyThrows
    @Override
    public SinkEvent deserialize(String topic, byte[] data) {
        try {
            return this.deserialize(data, new SinkEvent());
        } catch (Exception e) {
            log.warn("Error when SinkEventDeserializer deserialize e: ", e);
            Thread.sleep(ListenersConfigurationService.THROTTLING_TIMEOUT);
            throw e;
        }
    }

}
