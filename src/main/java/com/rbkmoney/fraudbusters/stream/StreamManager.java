package com.rbkmoney.fraudbusters.stream;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.streams.KafkaStreams;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

@Slf4j
@Service
@RequiredArgsConstructor
public class StreamManager {

    private final List<KafkaStreams> kafkaStreamsStorage = new ArrayList<>();

    private final FullToCompactStreamFactory fullToCompactStreamFactory;
    private final Properties rewriteStreamProperties;

    public void createStream(String fromTopic, String toTopic, String clientId) {
        KafkaStreams kafkaStreams = fullToCompactStreamFactory.create(fromTopic, toTopic, clientId,
                rewriteStreamProperties);
        kafkaStreams.start();
        log.info("Start stream kafkaStreams: {}", kafkaStreams.allMetadata());
        kafkaStreamsStorage.add(kafkaStreams);
    }

    public void stop() {
        kafkaStreamsStorage.forEach(KafkaStreams::close);
        log.info("StreamManager cleaned!");
    }

}
