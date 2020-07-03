package com.rbkmoney.fraudbusters.config;

import com.rbkmoney.damsel.fraudbusters.FraudPayment;
import com.rbkmoney.fraudbusters.config.properties.KafkaSslProperties;
import com.rbkmoney.fraudbusters.domain.FraudResult;
import com.rbkmoney.fraudbusters.domain.ScoresResult;
import com.rbkmoney.fraudbusters.fraud.model.P2PModel;
import com.rbkmoney.fraudbusters.util.SslKafkaUtils;
import com.rbkmoney.kafka.common.serialization.ThriftSerializer;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class KafkaTemplateConfig {

    @Value("${kafka.bootstrap.servers}")
    private String bootstrapServers;

    private final KafkaSslProperties kafkaSslProperties;

    private Map<String, Object> producerJsonConfigs() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        props.putAll(SslKafkaUtils.sslConfigure(kafkaSslProperties));
        return props;
    }

    private Map<String, Object> producerThriftConfigs() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, ThriftSerializer.class);
        props.putAll(SslKafkaUtils.sslConfigure(kafkaSslProperties));
        return props;
    }

    @Bean
    public KafkaTemplate<String, FraudResult> kafkaFraudResultTemplate() {
        return new KafkaTemplate<>(new DefaultKafkaProducerFactory<>(producerJsonConfigs()));
    }

    @Bean
    public KafkaTemplate<String, ScoresResult<P2PModel>> p2PModelKafkaTemplate() {
        return new KafkaTemplate<>(new DefaultKafkaProducerFactory<>(producerJsonConfigs()));
    }

    @Bean
    public KafkaTemplate<String, FraudPayment> kafkaFraudPaymentTemplate() {
        return new KafkaTemplate<>(new DefaultKafkaProducerFactory<>(producerThriftConfigs()));
    }

}
