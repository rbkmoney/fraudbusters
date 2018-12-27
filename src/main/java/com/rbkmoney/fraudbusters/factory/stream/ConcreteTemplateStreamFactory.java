package com.rbkmoney.fraudbusters.factory.stream;

import com.rbkmoney.fraudbusters.template.pool.StreamPool;
import com.rbkmoney.fraudo.FraudoParser;
import org.apache.kafka.streams.KafkaStreams;

import java.util.Properties;

public interface ConcreteTemplateStreamFactory {

    KafkaStreams create(final Properties streamsConfiguration, FraudoParser.ParseContext parseContext, String merchantId);
    KafkaStreams createDefault(final Properties streamsConfiguration, final StreamPool pool);

}
