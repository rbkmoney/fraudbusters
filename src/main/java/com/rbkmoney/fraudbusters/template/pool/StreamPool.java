package com.rbkmoney.fraudbusters.template.pool;

import org.apache.kafka.streams.KafkaStreams;

public interface StreamPool {

    void add(String key, KafkaStreams stream);

    KafkaStreams get(String key);

    void clear();

    void stopAndRemove(String key);

}
