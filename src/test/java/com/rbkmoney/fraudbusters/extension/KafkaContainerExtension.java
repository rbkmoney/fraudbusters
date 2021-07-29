package com.rbkmoney.fraudbusters.extension;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.utility.DockerImageName;


@Slf4j
public class KafkaContainerExtension implements BeforeAllCallback {

    public static KafkaContainer KAFKA;

    private static final String CONFLUENT_IMAGE_NAME = "confluentinc/cp-kafka";
    private static final String CONFLUENT_PLATFORM_VERSION = "6.0.3";

    @Override
    public void beforeAll(ExtensionContext extensionContext) {
        KAFKA = new org.testcontainers.containers.KafkaContainer(DockerImageName
                .parse(CONFLUENT_IMAGE_NAME)
                .withTag(CONFLUENT_PLATFORM_VERSION))
                .withEmbeddedZookeeper();
        KAFKA.start();
    }

}
