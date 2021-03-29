package com.rbkmoney.fraudbusters.util;

import com.rbkmoney.fraudbusters.config.properties.KafkaSslProperties;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.common.config.SslConfigs;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class SslKafkaUtils {

    private static final String PKCS_12 = "PKCS12";
    private static final String SSL = "SSL";

    public static Map<String, Object> sslConfigure(KafkaSslProperties kafkaSslProperties) {
        Map<String, Object> configProps = new HashMap<>();
        if (kafkaSslProperties.isEnable()) {
            configProps.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, SSL);
            configProps.put(
                    SslConfigs.SSL_TRUSTSTORE_LOCATION_CONFIG,
                    new File(kafkaSslProperties.getServerKeystoreLocation()).getAbsolutePath()
            );
            configProps.put(SslConfigs.SSL_TRUSTSTORE_PASSWORD_CONFIG, kafkaSslProperties.getServerPassword());
            configProps.put(SslConfigs.SSL_KEYSTORE_TYPE_CONFIG, PKCS_12);
            configProps.put(SslConfigs.SSL_TRUSTSTORE_TYPE_CONFIG, PKCS_12);
            configProps.put(
                    SslConfigs.SSL_KEYSTORE_LOCATION_CONFIG,
                    new File(kafkaSslProperties.getKeystoreLocation()).getAbsolutePath()
            );
            configProps.put(SslConfigs.SSL_KEYSTORE_PASSWORD_CONFIG, kafkaSslProperties.getKeystorePassword());
            configProps.put(SslConfigs.SSL_KEY_PASSWORD_CONFIG, kafkaSslProperties.getKeyPassword());
        }
        return configProps;
    }

}
