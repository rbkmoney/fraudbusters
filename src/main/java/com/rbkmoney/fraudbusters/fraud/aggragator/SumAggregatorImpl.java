package com.rbkmoney.fraudbusters.fraud.aggragator;

import com.rbkmoney.fraudbusters.constant.EventField;
import com.rbkmoney.fraudbusters.exception.RuleFunctionException;
import com.rbkmoney.fraudbusters.fraud.resolver.FieldResolver;
import com.rbkmoney.fraudbusters.repository.EventRepository;
import com.rbkmoney.fraudbusters.util.TimestampUtil;
import com.rbkmoney.fraudo.aggregator.SumAggregator;
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
public class SumAggregatorImpl implements SumAggregator {

    private final EventRepository eventRepository;
    private final FieldResolver fieldResolver;

    @Override
    public Double sum(CheckedField checkedField, FraudModel fraudModel, Long timeInMinutes) {
        return getSum(checkedField, fraudModel, timeInMinutes, eventRepository::sumOperationByField);
    }

    @Override
    public Double sum(CheckedField checkedField, FraudModel fraudModel, TimeWindow timeWindow, List<CheckedField> list) {
        return getSum(checkedField, fraudModel, timeWindow, list, eventRepository::sumOperationByFieldWithGroupBy);
    }

    @Override
    public Double sumSuccess(CheckedField checkedField, FraudModel fraudModel, Long timeInMinutes) {
        return getSum(checkedField, fraudModel, timeInMinutes, eventRepository::sumOperationSuccess);
    }

    @Override
    public Double sumSuccess(CheckedField checkedField, FraudModel fraudModel, TimeWindow timeWindow, List<CheckedField> list) {
        return getSum(checkedField, fraudModel, timeWindow, list, eventRepository::sumOperationSuccessWithGroupBy);
    }

    @Override
    public Double sumError(CheckedField checkedField, FraudModel fraudModel, Long timeInMinutes, String errorCode) {
        return getSum(checkedField, fraudModel, timeInMinutes, eventRepository::sumOperationError);
    }

    @Override
    public Double sumError(CheckedField checkedField, FraudModel fraudModel, TimeWindow timeWindow, String s, List<CheckedField> list) {
        return getSum(checkedField, fraudModel, timeWindow, list, eventRepository::sumOperationErrorWithGroupBy);
    }

    @NotNull
    private Double getSum(CheckedField checkedField, FraudModel fraudModel, Long timeInMinutes,
                          AggregateFunction<EventField, String, Long, Long, Long> aggregateFunction) {
        try {
            Instant now = Instant.now();
            FieldResolver.FieldModel resolve = fieldResolver.resolve(checkedField, fraudModel);
            Long sum = aggregateFunction.accept(resolve.getName(), resolve.getValue(),
                    TimestampUtil.generateTimestampMinusMinutes(now, timeInMinutes),
                    TimestampUtil.generateTimestampNow(now));
            double resultSum = (double) checkedLong(sum) + checkedLong(fraudModel.getAmount());
            log.debug("SumAggregatorImpl field: {} value: {}  sum: {}", resolve.getName(), resolve.getValue(), resultSum);
            return resultSum;
        } catch (Exception e) {
            log.warn("SumAggregatorImpl error when getCount e: ", e);
            throw new RuleFunctionException(e);
        }
    }

    @NotNull
    private Double getSum(CheckedField checkedField, FraudModel fraudModel, TimeWindow timeWindow, List<CheckedField> list,
                          AggregateGroupingFunction<EventField, String, Long, Long, List<FieldResolver.FieldModel>, Long> aggregateFunction) {
        try {
            Instant now = Instant.now();
            FieldResolver.FieldModel resolve = fieldResolver.resolve(checkedField, fraudModel);
            List<FieldResolver.FieldModel> eventFields = fieldResolver.resolveListFields(fraudModel, list);
            Long sum = aggregateFunction.accept(resolve.getName(), resolve.getValue(),
                    TimestampUtil.generateTimestampMinusMinutes(now, timeWindow.getStartWindowTime()),
                    TimestampUtil.generateTimestampMinusMinutes(now, timeWindow.getEndWindowTime()),
                    eventFields);
            double resultSum = (double) checkedLong(sum) + checkedLong(fraudModel.getAmount());
            log.debug("SumAggregatorImpl field: {} value: {}  sum: {}", resolve.getName(), resolve.getValue(), resultSum);
            return resultSum;
        } catch (Exception e) {
            log.warn("SumAggregatorImpl error when getCount e: ", e);
            throw new RuleFunctionException(e);
        }
    }

    private Long checkedLong(Long entry) {
        return entry != null ? entry : 0L;
    }
}
