package com.rbkmoney.fraudbusters.fraud.aggragator;

import com.rbkmoney.fraudbusters.aspect.BasicMetric;
import com.rbkmoney.fraudbusters.exception.RuleFunctionException;
import com.rbkmoney.fraudbusters.fraud.resolver.FieldResolver;
import com.rbkmoney.fraudbusters.repository.EventRepository;
import com.rbkmoney.fraudbusters.util.TimestampUtil;
import com.rbkmoney.fraudo.aggregator.UniqueValueAggregator;
import com.rbkmoney.fraudo.constant.CheckedField;
import com.rbkmoney.fraudo.model.FraudModel;
import com.rbkmoney.fraudo.model.TimeWindow;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class UniqueValueAggregatorImpl implements UniqueValueAggregator {

    private static final int CURRENT_ONE = 1;
    private final EventRepository eventRepository;
    private final FieldResolver fieldResolver;

    @Override
    @BasicMetric("countUniqueValue")
    public Integer countUniqueValue(CheckedField countField, FraudModel fraudModel, CheckedField onField, Long time) {
        try {
            Instant now = Instant.now();
            FieldResolver.FieldModel resolve = fieldResolver.resolve(countField, fraudModel);
            Integer uniqCountOperation = eventRepository.uniqCountOperation(resolve.getName(), resolve.getValue(), fieldResolver.resolve(onField), TimestampUtil.generateTimestampMinusMinutes(now, time),
                    TimestampUtil.generateTimestampNow(now));
            return uniqCountOperation + CURRENT_ONE;
        } catch (Exception e) {
            log.warn("UniqueValueAggregatorImpl error when getCount e: ", e);
            throw new RuleFunctionException(e);
        }
    }

    @Override
    @BasicMetric("countUniqueValueWindowed")
    public Integer countUniqueValue(CheckedField countField, FraudModel fraudModel, CheckedField onField, TimeWindow timeWindow, List<CheckedField> list) {
        try {
            Instant now = Instant.now();
            FieldResolver.FieldModel resolve = fieldResolver.resolve(countField, fraudModel);
            List<FieldResolver.FieldModel> fieldModels = fieldResolver.resolveListFields(fraudModel, list);
            Integer uniqCountOperation = eventRepository.uniqCountOperationWithGroupBy(resolve.getName(), resolve.getValue(),
                    fieldResolver.resolve(onField),
                    TimestampUtil.generateTimestampMinusMinutes(now, timeWindow.getStartWindowTime()),
                    TimestampUtil.generateTimestampMinusMinutes(now, timeWindow.getEndWindowTime()),
                    fieldModels);
            return uniqCountOperation + CURRENT_ONE;
        } catch (Exception e) {
            log.warn("UniqueValueAggregatorImpl error when getCount e: ", e);
            throw new RuleFunctionException(e);
        }
    }
}
