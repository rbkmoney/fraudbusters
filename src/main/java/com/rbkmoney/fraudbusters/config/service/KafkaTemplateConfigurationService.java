package com.rbkmoney.fraudbusters.config.service;

import com.rbkmoney.fraudbusters.config.properties.KafkaSslProperties;
import com.rbkmoney.fraudbusters.util.SslKafkaUtils;
import com.rbkmoney.kafka.common.serialization.ThriftSerializer;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class KafkaTemplateConfigurationService {

    @Value("${kafka.bootstrap.servers}")
    private String bootstrapServers;

    private final KafkaSslProperties kafkaSslProperties;

    public Map<String, Object> producerJsonConfigs() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        props.putAll(SslKafkaUtils.sslConfigure(kafkaSslProperties));
        return props;
    }

    public Map<String, Object> producerThriftConfigs() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, ThriftSerializer.class);
        props.putAll(SslKafkaUtils.sslConfigure(kafkaSslProperties));
        return props;
    }


}
