package com.rbkmoney.fraudbusters.fraud.aggragator;

import com.rbkmoney.fraudbusters.constant.EventField;
import com.rbkmoney.fraudbusters.fraud.resolver.FieldResolver;
import com.rbkmoney.fraudbusters.repository.EventRepository;
import com.rbkmoney.fraudbusters.util.TimestampUtil;
import com.rbkmoney.fraudo.aggregator.SumAggregator;
import com.rbkmoney.fraudo.constant.CheckedField;
import com.rbkmoney.fraudo.model.FraudModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;

@Slf4j
@RequiredArgsConstructor
public class SumAggregatorImpl implements SumAggregator {

    private final EventRepository eventRepository;
    private final FieldResolver fieldResolver;

    @Override
    public Double sum(CheckedField checkedField, FraudModel fraudModel, Long timeInMinutes) {
        return getSum(checkedField, fraudModel, timeInMinutes, eventRepository::sumOperationByField);
    }

    @Override
    public Double sumSuccess(CheckedField checkedField, FraudModel fraudModel, Long timeInMinutes) {
        return getSum(checkedField, fraudModel, timeInMinutes, eventRepository::sumOperationSuccess);
    }

    @Override
    public Double sumError(CheckedField checkedField, FraudModel fraudModel, Long timeInMinutes, String errorCode) {
        return getSum(checkedField, fraudModel, timeInMinutes, eventRepository::sumOperationError);
    }

    @NotNull
    private Double getSum(CheckedField checkedField, FraudModel fraudModel, Long timeInMinutes,
                          AggregateFunction<EventField, String, Long, Long, Long> aggregateFunction) {
        Instant now = Instant.now();
        FieldResolver.FieldModel resolve = fieldResolver.resolve(checkedField, fraudModel);
        Long sum = aggregateFunction.accept(resolve.getName(), resolve.getValue(), TimestampUtil.generateTimestampMinusMinutes(now, timeInMinutes),
                TimestampUtil.generateTimestampNow(now));
        log.debug("SumAggregatorImpl field: {} value: {}  count: {}", resolve.getName(), resolve.getValue(), sum);
        return sum != null ? sum / 100.0 : 0.0;
    }
}
