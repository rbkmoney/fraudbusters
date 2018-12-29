package com.rbkmoney.fraudbusters.stream;

import com.rbkmoney.fraudbusters.template.pool.StreamPool;
import com.rbkmoney.fraudo.FraudoParser;
import org.apache.kafka.streams.KafkaStreams;

import java.util.Properties;

public interface TemplateStreamFactory {

    KafkaStreams create(final Properties streamsConfiguration, FraudoParser.ParseContext parseContext, StreamPool pool);

}
