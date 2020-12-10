package com.rbkmoney.fraudbusters.serde;

import com.rbkmoney.damsel.fraudbusters.FraudPayment;
import com.rbkmoney.fraudbusters.config.service.ListenersConfigurationService;
import com.rbkmoney.kafka.common.serialization.AbstractThriftDeserializer;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FraudPaymentDeserializer extends AbstractThriftDeserializer<FraudPayment> {

    @SneakyThrows
    @Override
    public FraudPayment deserialize(String topic, byte[] data) {
        try {
            return deserialize(data, new FraudPayment());
        } catch (Exception e) {
            log.warn("Error when PaymentDeserializer deserialize e: ", e);
            Thread.sleep(ListenersConfigurationService.THROTTLING_TIMEOUT);
            throw e;
        }
    }

}
