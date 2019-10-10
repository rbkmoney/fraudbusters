package com.rbkmoney.fraudbusters.config;

import com.rbkmoney.fraudbusters.domain.FraudResult;
import com.rbkmoney.fraudbusters.serde.FraudResultSerializer;
import com.rbkmoney.fraudbusters.util.SslKafkaUtils;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.HashMap;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class ResultTemplateConfig {

    @Value("${kafka.bootstrap.servers}")
    private String bootstrapServers;

    @Value("${kafka.ssl.server-password}")
    private String serverStorePassword;

    @Value("${kafka.ssl.server-keystore-location}")
    private String serverStoreCertPath;

    @Value("${kafka.ssl.keystore-password}")
    private String keyStorePassword;

    @Value("${kafka.ssl.key-password}")
    private String keyPassword;

    @Value("${kafka.ssl.keystore-location}")
    private String clientStoreCertPath;

    @Value("${kafka.ssl.enable}")
    private boolean kafkaSslEnable;

    private Map<String, Object> producerFraudResultConfigs() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, FraudResultSerializer.class);
        props.putAll(SslKafkaUtils.sslConfigure(kafkaSslEnable, serverStoreCertPath, serverStorePassword,
                clientStoreCertPath, keyStorePassword, keyPassword));
        return props;
    }

    @Bean
    public KafkaTemplate<String, FraudResult> kafkaFraudResultTemplate() {
        return new KafkaTemplate<>(new DefaultKafkaProducerFactory<>(producerFraudResultConfigs()));
    }

}
