package com.rbkmoney.fraudbusters.serde;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rbkmoney.fraudbusters.domain.RuleTemplate;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Deserializer;

import java.util.Map;

@Slf4j
public class RuleTemplateDeserializer implements Deserializer<RuleTemplate> {

    private final ObjectMapper om = new ObjectMapper();

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {

    }

    @Override
    public RuleTemplate deserialize(String topic, byte[] data) {
        RuleTemplate ruleTemplate = null;
        try {
            ruleTemplate = om.readValue(data, RuleTemplate.class);
        } catch (Exception e) {
            log.error("Error when deserialize ruleTemplate data: {} e: ", data, e);
        }
        return ruleTemplate;
    }

    @Override
    public void close() {

    }

}
