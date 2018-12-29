package com.rbkmoney.fraudbusters.stream;

import com.rbkmoney.fraudo.FraudoParser;
import org.apache.kafka.streams.KafkaStreams;

import java.util.Properties;

public interface ConcreteTemplateStreamFactory {

    KafkaStreams create(final Properties streamsConfiguration, FraudoParser.ParseContext parseContext, String merchantId);

}
