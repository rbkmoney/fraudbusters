package com.rbkmoney.fraudbusters.config.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "kafka.topic")
public class KafkaTopics {

    private String template;
    private String reference;
    private String groupList;
    private String groupReference;

    private String fullTemplate;
    private String fullReference;
    private String fullGroupList;
    private String fullGroupReference;

}
