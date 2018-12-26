package com.rbkmoney.fraudbusters.factory.stream;

import com.rbkmoney.fraudbusters.domain.FraudResult;
import com.rbkmoney.fraudbusters.serde.FraudoModelSerde;
import com.rbkmoney.fraudbusters.template.FraudHandler;
import com.rbkmoney.fraudo.FraudoParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Properties;

@Slf4j
@Component
@RequiredArgsConstructor
public class ConcreteStreamFactory implements ConcreteTemplateStreamFactory {

    @Value("${kafka.concrete.stream.topic}")
    private String readTopic;
    @Value("${kafka.result.stream.topic}")
    private String resultTopic;
    private final FraudHandler fraudHandler;

    private final FraudoModelSerde fraudoModelSerde = new FraudoModelSerde();

    @Override
    public KafkaStreams create(final Properties streamsConfiguration, FraudoParser.ParseContext parseContext, String merchantId) {
        StreamsBuilder builder = new StreamsBuilder();
        builder.stream(readTopic, Consumed.with(Serdes.String(), fraudoModelSerde))
                .peek((s, fraudModel) -> log.debug("Concrete stream check merchantId: {} and fraudModel: {}", merchantId, fraudModel))
                .filter((k, v) -> merchantId.equals(v.getPartyId()))
                .mapValues(fraudModel -> new FraudResult(fraudModel, fraudHandler.handle(parseContext, fraudModel)))
                .to(resultTopic);
        return new KafkaStreams(builder.build(), streamsConfiguration);
    }

}
