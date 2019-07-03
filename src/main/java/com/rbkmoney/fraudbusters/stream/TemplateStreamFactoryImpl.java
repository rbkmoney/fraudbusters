package com.rbkmoney.fraudbusters.stream;

import com.rbkmoney.fraudbusters.domain.FraudResult;
import com.rbkmoney.fraudbusters.exception.StreamInitializationException;
import com.rbkmoney.fraudbusters.serde.FraudRequestSerde;
import com.rbkmoney.fraudbusters.serde.FraudoResultSerde;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.Produced;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Properties;

@Slf4j
@Component
@RequiredArgsConstructor
public class TemplateStreamFactoryImpl implements TemplateStreamFactory {

    @Value("${kafka.topic.global}")
    private String readTopic;

    @Value("${kafka.topic.result}")
    private String resultTopic;

    private final FraudRequestSerde fraudRequestSerde = new FraudRequestSerde();
    private final TemplateVisitorImpl templateVisitor;

    @Override
    public KafkaStreams create(final Properties streamsConfiguration) {
        try {
            StreamsBuilder builder = new StreamsBuilder();
            builder.stream(readTopic, Consumed.with(Serdes.String(), fraudRequestSerde))
                    .filter((s, fraudRequest) -> fraudRequest != null && fraudRequest.getFraudModel() != null)
                    .peek((s, fraudRequest) -> log.info("Global stream check fraudRequest: {}", fraudRequest))
                    .mapValues(fraudRequest -> new FraudResult(fraudRequest, templateVisitor.visit(fraudRequest.getFraudModel())))
                    .to(resultTopic, Produced.with(Serdes.String(), new FraudoResultSerde()));
            KafkaStreams kafkaStreams = new KafkaStreams(builder.build(), streamsConfiguration);
            kafkaStreams.start();
            return kafkaStreams;
        } catch (Exception e) {
            log.error("Error when GlobalStreamFactory insert e: ", e);
            throw new StreamInitializationException(e);
        }
    }

}
