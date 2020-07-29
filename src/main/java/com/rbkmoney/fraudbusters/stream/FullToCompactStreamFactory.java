package com.rbkmoney.fraudbusters.stream;

import com.rbkmoney.fraudbusters.serde.CommandSerde;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.Produced;
import org.springframework.stereotype.Component;

import java.util.Properties;

@Slf4j
@Component
@RequiredArgsConstructor
public class FullToCompactStreamFactory {

    private final CommandSerde commandSerde = new CommandSerde();

    public KafkaStreams create(String fromTopic, String toTopic, String clientId, final Properties streamsConfiguration) {
        try {
            streamsConfiguration.setProperty(StreamsConfig.APPLICATION_ID_CONFIG, clientId);
            streamsConfiguration.setProperty(StreamsConfig.CLIENT_ID_CONFIG, clientId);
            StreamsBuilder builder = new StreamsBuilder();
            builder.stream(fromTopic, Consumed.with(Serdes.String(), commandSerde))
                    .peek((key, value) -> log.debug("FullToCompactStreamFactory key: {}", key))
                    .to(toTopic, Produced.with(Serdes.String(), commandSerde));
            return new KafkaStreams(builder.build(), streamsConfiguration);
        } catch (Exception e) {
            log.error("WbListStreamFactory error when create stream e: ", e);
            throw new RuntimeException(e);
        }
    }

}
