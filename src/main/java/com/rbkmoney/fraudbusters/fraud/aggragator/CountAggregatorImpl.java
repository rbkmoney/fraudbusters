package com.rbkmoney.fraudbusters.fraud.aggragator;

import com.rbkmoney.fraudbusters.repository.FraudResultRepository;
import com.rbkmoney.fraudo.aggregator.CountAggregator;
import com.rbkmoney.fraudo.constant.CheckedField;
import lombok.RequiredArgsConstructor;

import java.time.Instant;
import java.time.ZoneId;

@RequiredArgsConstructor
public class CountAggregatorImpl implements CountAggregator {

    private final FraudResultRepository fraudResultRepository;

    @Override
    public Integer count(CheckedField checkedField, String s, Long aLong) {
        Instant now = Instant.now();
        long l = now.atZone(ZoneId.systemDefault())
                .withSecond(0)
                .withNano(0).toInstant().toEpochMilli();
        long from = now.atZone(ZoneId.systemDefault())
                .minusMinutes(aLong)
                .withSecond(0)
                .withNano(0).toInstant().toEpochMilli();
        return fraudResultRepository.countOperationByEmail(s, from, l);
    }

    @Override
    public Integer countSuccess(CheckedField checkedField, String s, Long aLong) {
        return null;
    }

    @Override
    public Integer countError(CheckedField checkedField, String s, Long aLong, String s1) {
        return null;
    }
}
