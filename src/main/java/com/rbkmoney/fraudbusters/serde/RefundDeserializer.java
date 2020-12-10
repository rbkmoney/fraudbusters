package com.rbkmoney.fraudbusters.serde;


import com.rbkmoney.damsel.fraudbusters.Refund;
import com.rbkmoney.fraudbusters.config.service.ListenersConfigurationService;
import com.rbkmoney.kafka.common.serialization.AbstractThriftDeserializer;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RefundDeserializer extends AbstractThriftDeserializer<Refund> {

    @SneakyThrows
    @Override
    public Refund deserialize(String topic, byte[] data) {
        try {
            return deserialize(data, new Refund());
        } catch (Exception e) {
            log.warn("Error when RefundDeserializer deserialize e: ", e);
            Thread.sleep(ListenersConfigurationService.THROTTLING_TIMEOUT);
            throw e;
        }
    }

}
