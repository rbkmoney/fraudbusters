package com.rbkmoney.fraudbusters.factory.stream;

import com.rbkmoney.fraudbusters.domain.FraudResult;
import com.rbkmoney.fraudbusters.serde.FraudoModelSerde;
import com.rbkmoney.fraudbusters.serde.FraudoResultSerde;
import com.rbkmoney.fraudo.FraudoParser;
import com.rbkmoney.fraudo.aggregator.CountAggregator;
import com.rbkmoney.fraudo.aggregator.SumAggregator;
import com.rbkmoney.fraudo.aggregator.UniqueValueAggregator;
import com.rbkmoney.fraudo.constant.ResultStatus;
import com.rbkmoney.fraudo.factory.FastFraudVisitorFactory;
import com.rbkmoney.fraudo.factory.FraudVisitorFactory;
import com.rbkmoney.fraudo.finder.InListFinder;
import com.rbkmoney.fraudo.model.FraudModel;
import com.rbkmoney.fraudo.resolver.CountryResolver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Produced;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Properties;

@Slf4j
@Component
@RequiredArgsConstructor
public class GlobalStreamFactory implements TemplateStreamFactory {

    @Value("${kafka.global.stream.topic}")
    private String readTopic;
    @Value("${kafka.result.stream.topic}")
    private String resultTopic;
    @Value("${kafka.concrete.stream.topic}")
    private String concreteTopic;

    private final FraudVisitorFactory fraudVisitorFactory = new FastFraudVisitorFactory();
    private final FraudoModelSerde fraudoModelSerde = new FraudoModelSerde();

    private final CountAggregator countAggregator;
    private final SumAggregator sumAggregator;
    private final UniqueValueAggregator uniqueValueAggregator;
    private final CountryResolver countryResolver;
    private final InListFinder blackListFinder;
    private final InListFinder whiteListFinder;

    @Override
    public KafkaStreams create(final Properties streamsConfiguration, FraudoParser.ParseContext parseContext) {
        try {
            StreamsBuilder builder = new StreamsBuilder();
            KStream<String, FraudResult>[] branch = builder
                    .stream(readTopic, Consumed.with(Serdes.String(), fraudoModelSerde))
                    .peek((s, fraudModel) -> log.debug("Global stream check fraudModel: {}", fraudModel))
                    .mapValues(fraudModel -> new FraudResult(fraudModel, applyRules(parseContext, fraudModel)))
                    .branch((k, v) -> ResultStatus.ACCEPT.equals(v.getResultStatus()),
                            (k, v) -> !ResultStatus.ACCEPT.equals(v.getResultStatus()));
            branch[0].to(resultTopic, Produced.with(Serdes.String(), new FraudoResultSerde()));
            branch[1].mapValues(FraudResult::getFraudModel).to(concreteTopic);
            return new KafkaStreams(builder.build(), streamsConfiguration);
        } catch (Exception e) {
            log.error("Error when GlobalStreamFactory create e: ", e);
            throw new RuntimeException(e);
        }
    }

    private ResultStatus applyRules(FraudoParser.ParseContext parseContext, FraudModel fraudModel) {
        return (ResultStatus) fraudVisitorFactory.createVisitor(fraudModel, countAggregator, sumAggregator,
                uniqueValueAggregator, countryResolver, blackListFinder, whiteListFinder).visit(parseContext);
    }
}
