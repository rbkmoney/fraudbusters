package com.rbkmoney.fraudbusters.util;

import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.common.config.SslConfigs;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class SslKafkaUtils {

    private static final String PKCS_12 = "PKCS12";
    private static final String SSL = "SSL";

    public static Map<String, Object> sslConfigure(Boolean kafkaSslEnable, String serverStoreCertPath, String serverStorePassword,
                                                   String clientStoreCertPath, String keyStorePassword, String keyPassword) {
        Map<String, Object> configProps = new HashMap<>();
        if (kafkaSslEnable) {
            configProps.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, SSL);
            configProps.put(SslConfigs.SSL_TRUSTSTORE_LOCATION_CONFIG, new File(serverStoreCertPath).getAbsolutePath());
            configProps.put(SslConfigs.SSL_TRUSTSTORE_PASSWORD_CONFIG, serverStorePassword);
            configProps.put(SslConfigs.SSL_KEYSTORE_TYPE_CONFIG, PKCS_12);
            configProps.put(SslConfigs.SSL_TRUSTSTORE_TYPE_CONFIG, PKCS_12);
            configProps.put(SslConfigs.SSL_KEYSTORE_LOCATION_CONFIG, new File(clientStoreCertPath).getAbsolutePath());
            configProps.put(SslConfigs.SSL_KEYSTORE_PASSWORD_CONFIG, keyStorePassword);
            configProps.put(SslConfigs.SSL_KEY_PASSWORD_CONFIG, keyPassword);
        }
        return configProps;
    }

}
