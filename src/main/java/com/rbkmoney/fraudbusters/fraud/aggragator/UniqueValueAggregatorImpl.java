package com.rbkmoney.fraudbusters.fraud.aggragator;

import com.rbkmoney.fraudbusters.exception.RuleFunctionException;
import com.rbkmoney.fraudbusters.fraud.resolver.FieldResolver;
import com.rbkmoney.fraudbusters.repository.EventRepository;
import com.rbkmoney.fraudbusters.util.TimestampUtil;
import com.rbkmoney.fraudo.aggregator.UniqueValueAggregator;
import com.rbkmoney.fraudo.constant.CheckedField;
import com.rbkmoney.fraudo.model.FraudModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;

@Slf4j
@RequiredArgsConstructor
public class UniqueValueAggregatorImpl implements UniqueValueAggregator {

    private static final int CURRENT_ONE = 1;
    private final EventRepository eventRepository;
    private final FieldResolver fieldResolver;

    @Override
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
}
