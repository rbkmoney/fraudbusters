package com.rbkmoney.fraudbusters.fraud.aggragator;

import com.rbkmoney.fraudbusters.constant.EventField;
import com.rbkmoney.fraudbusters.fraud.resolver.FieldResolver;
import com.rbkmoney.fraudbusters.repository.EventRepository;
import com.rbkmoney.fraudbusters.util.TimestampUtil;
import com.rbkmoney.fraudo.aggregator.CountAggregator;
import com.rbkmoney.fraudo.constant.CheckedField;
import com.rbkmoney.fraudo.model.FraudModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;
import java.time.LocalDateTime;

@Slf4j
@RequiredArgsConstructor
public class CountAggregatorImpl implements CountAggregator {

    private final EventRepository eventRepository;
    private final FieldResolver fieldResolver;

    @Override
    public Integer count(CheckedField checkedField, FraudModel fraudModel, Long aLong) {
        return getCount(checkedField, fraudModel, aLong, eventRepository::countOperationByField);
    }

    @Override
    public Integer countSuccess(CheckedField checkedField, FraudModel fraudModel, Long aLong) {
        return getCount(checkedField, fraudModel, aLong, eventRepository::countOperationSuccess);
    }

    @Override
    public Integer countError(CheckedField checkedField, FraudModel fraudModel, Long aLong, String s) {
        return getCount(checkedField, fraudModel, aLong, eventRepository::countOperationError);
    }

    @NotNull
    private Integer getCount(CheckedField checkedField, FraudModel fraudModel, Long aLong,
                             AggregateFunction<EventField, String, Long, Long, Integer> aggregateFunction) {
        Instant now = Instant.now();
        FieldResolver.FieldModel resolve = fieldResolver.resolve(checkedField, fraudModel);
        Integer count = aggregateFunction.accept(resolve.getName(), resolve.getValue(), TimestampUtil.generateTimestampMinusMinutes(now, aLong),
                TimestampUtil.generateTimestampNow(now));
        log.debug("CountAggregatorImpl field: {} value: {}  count: {}", resolve.getName(), resolve.getValue(), count);
        return count;
    }
}
