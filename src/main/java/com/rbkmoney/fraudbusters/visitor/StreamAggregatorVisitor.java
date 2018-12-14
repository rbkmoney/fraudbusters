package com.rbkmoney.fraudbusters.visitor;

import com.rbkmoney.fraudo.FraudoBaseVisitor;
import com.rbkmoney.fraudo.FraudoParser;
import com.rbkmoney.fraudo.constant.CheckedField;
import com.rbkmoney.fraudo.model.FraudModel;
import com.rbkmoney.fraudo.utils.TextUtil;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.*;

import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
public class StreamAggregatorVisitor extends FraudoBaseVisitor<StreamsBuilder> {

    private KafkaStreams streams = null;
    private final StreamsBuilder builder = new StreamsBuilder();
    private final String partyId;

    @Override
    public StreamsBuilder visitCount(FraudoParser.CountContext ctx) {
        String countTarget = TextUtil.safeGetText(ctx.STRING());
        String time = TextUtil.safeGetText(ctx.DECIMAL());

        final KStream<String, FraudModel> views = builder.stream("test");

        final KTable<Windowed<String>, Long> anomalousUsers = views
                .filter((ignoredKey, model) -> partyId.equals(model.getPartyId()))
                .map((ignoredKey, username) -> new KeyValue<>(username.getIp(), username.getIp()))
                .groupByKey()
                .windowedBy(TimeWindows.of(TimeUnit.MINUTES.toMillis(Long.getLong(time))))
                .count();

        final KStream<String, Long> anomalousUsersForConsole = anomalousUsers
                .toStream()
                .filter((windowedUserId, count) -> count != null)
                .map((windowedUserId, count) -> new KeyValue<>(windowedUserId.toString(), count));

        final Serde<String> stringSerde = Serdes.String();
        final Serde<Long> longSerde = Serdes.Long();

        anomalousUsersForConsole.to("AnomalousUsers", Produced.with(stringSerde, longSerde));

        return builder;
    }

    @Override
    public StreamsBuilder visitCount_success(FraudoParser.Count_successContext ctx) {
        return super.visitCount_success(ctx);
    }

    @Override
    public StreamsBuilder visitCount_error(FraudoParser.Count_errorContext ctx) {
        return super.visitCount_error(ctx);
    }

    @Override
    public StreamsBuilder visitSum(FraudoParser.SumContext ctx) {
        return super.visitSum(ctx);
    }

    @Override
    public StreamsBuilder visitSum_success(FraudoParser.Sum_successContext ctx) {
        return super.visitSum_success(ctx);
    }

    @Override
    public StreamsBuilder visitSum_error(FraudoParser.Sum_errorContext ctx) {
        return super.visitSum_error(ctx);
    }
}
