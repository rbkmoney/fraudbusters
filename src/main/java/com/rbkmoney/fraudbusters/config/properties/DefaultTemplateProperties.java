package com.rbkmoney.fraudbusters.config.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "defaultTemplate")
public class DefaultTemplateProperties {
    private boolean enable;
    private int countToCheckYears;
}
