package com.rbkmoney.fraudbusters.fraud.aggragator;

import com.rbkmoney.fraudbusters.fraud.resolver.FieldResolver;
import com.rbkmoney.fraudbusters.repository.EventRepository;
import com.rbkmoney.fraudo.aggregator.UniqueValueAggregator;
import com.rbkmoney.fraudo.constant.CheckedField;
import com.rbkmoney.fraudo.model.FraudModel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class UniqueValueAggregatorImpl implements UniqueValueAggregator {

    private final EventRepository eventRepository;
    private final FieldResolver fieldResolver;

    @Override
    public Integer countUniqueValue(CheckedField countField, FraudModel fraudModel, CheckedField onField) {
        FieldResolver.FieldModel resolve = fieldResolver.resolve(countField, fraudModel);
        return eventRepository.uniqCountOperation(resolve.getName(), resolve.getValue(), fieldResolver.resolve(onField));
    }
}
