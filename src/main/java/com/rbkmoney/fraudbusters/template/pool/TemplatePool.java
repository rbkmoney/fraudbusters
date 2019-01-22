package com.rbkmoney.fraudbusters.template.pool;

import org.apache.kafka.streams.KafkaStreams;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class TemplatePool implements StreamPool {

    public static final long WAIT_TIME = 10L;
    private Map<String, KafkaStreams> streams = new ConcurrentHashMap<>();

    @Override
    public void add(String key, KafkaStreams stream) {
        KafkaStreams kafkaStreams = restartStream(key, stream);
        streams.put(key, kafkaStreams);
    }

    @Override
    public KafkaStreams get(String key) {
        return streams.get(key);
    }

    @Override
    public void clear() {
        streams.forEach((key, value) -> value.close(Duration.ofSeconds(WAIT_TIME)));
    }

    @Override
    public void stopAndRemove(String key) {
        KafkaStreams kafkaStreams = streams.get(key);
        if (kafkaStreams != null && kafkaStreams.state().isRunning()) {
            kafkaStreams.close(Duration.ofSeconds(WAIT_TIME));
            streams.remove(key);
        }
    }

    private KafkaStreams restartStream(String key, KafkaStreams newStream) {
        stopAndRemove(key);
        newStream.start();
        return newStream;
    }
}
