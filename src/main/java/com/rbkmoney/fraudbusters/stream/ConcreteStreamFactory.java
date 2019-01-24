package com.rbkmoney.fraudbusters.stream;

import com.rbkmoney.fraudbusters.domain.FraudResult;
import com.rbkmoney.fraudbusters.serde.FraudRequestSerde;
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

    private final FraudRequestSerde fraudRequestSerde = new FraudRequestSerde();

    @Override
    public KafkaStreams create(final Properties streamsConfiguration, FraudoParser.ParseContext parseContext, String merchantId) {
        StreamsBuilder builder = new StreamsBuilder();
        builder.stream(readTopic, Consumed.with(Serdes.String(), fraudRequestSerde))
                .filter((s, fraudRequest) -> fraudRequest != null)
                .peek((s, fraudRequest) -> log.debug("Concrete stream check merchantId: {} and fraudRequest: {}", merchantId, fraudRequest))
                .filter((k, v) -> merchantId.equals(v.getFraudModel().getPartyId()))
                .mapValues(fraudRequest -> new FraudResult(fraudRequest, fraudHandler.handle(parseContext, fraudRequest.getFraudModel())))
                .to(resultTopic);
        return new KafkaStreams(builder.build(), streamsConfiguration);
    }

}
