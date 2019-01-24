package com.rbkmoney.fraudbusters.stream;

import com.rbkmoney.fraudbusters.domain.FraudResult;
import com.rbkmoney.fraudbusters.serde.FraudRequestSerde;
import com.rbkmoney.fraudbusters.serde.FraudoResultSerde;
import com.rbkmoney.fraudbusters.template.pool.StreamPool;
import com.rbkmoney.fraudo.FraudoParser;
import com.rbkmoney.fraudo.aggregator.CountAggregator;
import com.rbkmoney.fraudo.aggregator.SumAggregator;
import com.rbkmoney.fraudo.aggregator.UniqueValueAggregator;
import com.rbkmoney.fraudo.constant.ResultStatus;
import com.rbkmoney.fraudo.factory.FastFraudVisitorFactory;
import com.rbkmoney.fraudo.factory.FraudVisitorFactory;
import com.rbkmoney.fraudo.finder.InListFinder;
import com.rbkmoney.fraudo.model.FraudModel;
import com.rbkmoney.fraudo.model.ResultModel;
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
    private final FraudRequestSerde fraudRequestSerde = new FraudRequestSerde();

    private final CountAggregator countAggregator;
    private final SumAggregator sumAggregator;
    private final UniqueValueAggregator uniqueValueAggregator;
    private final CountryResolver countryResolver;
    private final InListFinder blackListFinder;
    private final InListFinder whiteListFinder;

    @Override
    public KafkaStreams create(final Properties streamsConfiguration, FraudoParser.ParseContext parseContext, StreamPool pool) {
        try {
            StreamsBuilder builder = new StreamsBuilder();
            KStream<String, FraudResult>[] branch = builder
                    .stream(readTopic, Consumed.with(Serdes.String(), fraudRequestSerde))
                    .filter((s, fraudRequest) -> fraudRequest != null && fraudRequest.getFraudModel() != null)
                    .peek((s, fraudRequest) -> log.debug("Global stream check fraudRequest: {}", fraudRequest))
                    .mapValues(fraudRequest -> new FraudResult(fraudRequest, applyRules(parseContext, fraudRequest.getFraudModel())))
                    .branch((k, v) -> !isNormal(v) || pool.get(v.getFraudRequest().getFraudModel().getPartyId()) == null,
                            (k, v) -> isNormal(v));
            branch[0].to(resultTopic, Produced.with(Serdes.String(), new FraudoResultSerde()));
            branch[1].mapValues(FraudResult::getFraudRequest).to(concreteTopic);
            return new KafkaStreams(builder.build(), streamsConfiguration);
        } catch (Exception e) {
            log.error("Error when GlobalStreamFactory insert e: ", e);
            throw new RuntimeException(e);
        }
    }

    private boolean isNormal(FraudResult v) {
        return ResultStatus.NORMAL.equals(v.getResultModel().getResultStatus());
    }

    private ResultModel applyRules(FraudoParser.ParseContext parseContext, FraudModel fraudModel) {
        return (ResultModel) fraudVisitorFactory.createVisitor(fraudModel, countAggregator, sumAggregator,
                uniqueValueAggregator, countryResolver, blackListFinder, whiteListFinder).visit(parseContext);
    }
}
