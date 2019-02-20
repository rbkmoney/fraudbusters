package com.rbkmoney.fraudbusters.stream;

import org.apache.kafka.streams.KafkaStreams;

import java.util.Properties;

public interface TemplateStreamFactory {

    KafkaStreams create(final Properties streamsConfiguration);

}
