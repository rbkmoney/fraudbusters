package com.rbkmoney.fraudbusters.stream.aggregate;

import com.rbkmoney.fraudbusters.domain.MgEventSinkRow;
import com.rbkmoney.fraudbusters.exception.StreamInitializationException;
import com.rbkmoney.fraudbusters.serde.MgEventSinkRowSerde;
import com.rbkmoney.fraudbusters.serde.SinkEventSerde;
import com.rbkmoney.fraudbusters.stream.TemplateStreamFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.Produced;
import org.junit.Before;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Properties;

@Slf4j
@Component
@RequiredArgsConstructor
public class EventSinkAggregationStreamFactoryImpl implements TemplateStreamFactory {

    public static final String IN_MEMORY = "in-memory-aggregates";
    @Value("${kafka.topic.event.sink.initial}")
    private String initialEventSink;

    @Value("${kafka.topic.event.sink.aggregated}")
    private String aggregatedSinkTopic;

    private final SinkEventSerde sinkEventSerde = new SinkEventSerde();
    private final MgEventSinkRowSerde mgEventSinkRowSerde = new MgEventSinkRowSerde();
    private final Serde<String> stringSerde = Serdes.String();
    private final MgEventAggregator mgEventAggregator;
    private final MgEventSinkRowMapper mgEventSinkRowMapper;

    @Override
    public KafkaStreams create(final Properties streamsConfiguration) {
        try {
            log.info("Create stream aggregation!");

            StreamsBuilder builder = new StreamsBuilder();
            builder.stream(initialEventSink, Consumed.with(Serdes.String(), sinkEventSerde))
                    .peek((key, value) -> log.debug("Aggregate key={} value={}", key, value))
                    .map(mgEventSinkRowMapper)
                    .groupByKey()
                    .aggregate(MgEventSinkRow::new, mgEventAggregator, Materialized.with(stringSerde, mgEventSinkRowSerde))
                    .toStream()
                    .filter((key, value) -> value.getResultStatus() != null)
                    .peek((key, value) -> log.debug("Filtered key={} value={}", key, value))
                    .to(aggregatedSinkTopic, Produced.with(stringSerde, mgEventSinkRowSerde));

            KafkaStreams kafkaStreams = new KafkaStreams(builder.build(), streamsConfiguration);
            kafkaStreams.setUncaughtExceptionHandler((t, e) -> {
                log.error("Caught unhandled Kafka Streams Exception:", e);
                kafkaStreams.close();
            });
            kafkaStreams.start();
            log.info("Stream aggregation is started!");
            return kafkaStreams;
        } catch (Exception e) {
            log.error("Error when EventSinkAggregationStreamFactoryImpl insert e: ", e);
            throw new StreamInitializationException(e);
        }
    }

}
