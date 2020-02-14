package com.rbkmoney.fraudbusters.config.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "kafka.ssl")
public class KafkaSslProperties {

    private String serverPassword;
    private String serverKeystoreLocation;
    private String keystorePassword;
    private String keyPassword;
    private String keystoreLocation;

    private boolean enable;

}
