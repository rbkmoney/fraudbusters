package com.rbkmoney.fraudbusters.fraud.aggragator;

import com.rbkmoney.fraudbusters.constant.EventField;
import com.rbkmoney.fraudbusters.exception.RuleFunctionException;
import com.rbkmoney.fraudbusters.fraud.resolver.FieldResolver;
import com.rbkmoney.fraudbusters.repository.EventRepository;
import com.rbkmoney.fraudbusters.repository.MgEventSinkRepository;
import com.rbkmoney.fraudbusters.util.TimestampUtil;
import com.rbkmoney.fraudo.aggregator.CountAggregator;
import com.rbkmoney.fraudo.constant.CheckedField;
import com.rbkmoney.fraudo.model.FraudModel;
import com.rbkmoney.fraudo.model.TimeWindow;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class CountAggregatorImpl implements CountAggregator {

    private static final int CURRENT_ONE = 1;
    private final EventRepository eventRepository;
    private final MgEventSinkRepository mgEventSinkRepository;
    private final FieldResolver fieldResolver;

    @Override
    public Integer count(CheckedField checkedField, FraudModel fraudModel, Long aLong) {
        return getCount(checkedField, fraudModel, aLong, eventRepository::countOperationByField);
    }

    @Override
    public Integer count(CheckedField checkedField, FraudModel fraudModel, TimeWindow timeWindow, List<CheckedField> list) {
        return getCount(checkedField, fraudModel, timeWindow, list, eventRepository::countOperationByFieldWithGroupBy);
    }

    @Override
    public Integer countSuccess(CheckedField checkedField, FraudModel fraudModel, Long aLong) {
        return getCount(checkedField, fraudModel, aLong, mgEventSinkRepository::countOperationSuccess);
    }

    @Override
    public Integer countSuccess(CheckedField checkedField, FraudModel fraudModel, TimeWindow timeWindow, List<CheckedField> list) {
        return getCount(checkedField, fraudModel, timeWindow, list, mgEventSinkRepository::countOperationSuccessWithGroupBy);
    }

    @Override
    public Integer countError(CheckedField checkedField, FraudModel fraudModel, Long aLong, String s) {
        return getCount(checkedField, fraudModel, aLong, mgEventSinkRepository::countOperationError);
    }

    @Override
    public Integer countError(CheckedField checkedField, FraudModel fraudModel, TimeWindow timeWindow, String s, List<CheckedField> list) {
        return getCount(checkedField, fraudModel, timeWindow, list, mgEventSinkRepository::countOperationErrorWithGroupBy);
    }

    @NotNull
    private Integer getCount(CheckedField checkedField, FraudModel fraudModel, Long aLong,
                             AggregateFunction<String, String, Long, Long, Integer> aggregateFunction) {
        try {
            Instant now = Instant.now();
            FieldResolver.FieldModel resolve = fieldResolver.resolve(checkedField, fraudModel);
            Integer count = aggregateFunction.accept(resolve.getName(), resolve.getValue(),
                    TimestampUtil.generateTimestampMinusMinutes(now, aLong),
                    TimestampUtil.generateTimestampNow(now));
            log.debug("CountAggregatorImpl field: {} value: {}  count: {}", resolve.getName(), resolve.getValue(), count);
            return count + CURRENT_ONE;
        } catch (Exception e) {
            log.warn("CountAggregatorImpl error when getCount e: ", e);
            throw new RuleFunctionException(e);
        }
    }

    @NotNull
    private Integer getCount(CheckedField checkedField, FraudModel fraudModel, TimeWindow timeWindow, List<CheckedField> list,
                             AggregateGroupingFunction<String, String, Long, Long, List<FieldResolver.FieldModel>, Integer> aggregateFunction) {
        try {
            Instant now = Instant.now();
            FieldResolver.FieldModel resolve = fieldResolver.resolve(checkedField, fraudModel);
            List<FieldResolver.FieldModel> eventFields = fieldResolver.resolveListFields(fraudModel, list);
            Integer count = aggregateFunction.accept(resolve.getName(), resolve.getValue(),
                    TimestampUtil.generateTimestampMinusMinutes(now, timeWindow.getStartWindowTime()),
                    TimestampUtil.generateTimestampMinusMinutes(now, timeWindow.getEndWindowTime()),
                    eventFields);
            log.debug("CountAggregatorImpl field: {} value: {}  count: {}", resolve.getName(), resolve.getValue(), count);
            return count + CURRENT_ONE;
        } catch (Exception e) {
            log.warn("CountAggregatorImpl error when getCount e: ", e);
            throw new RuleFunctionException(e);
        }
    }
}
