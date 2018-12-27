package com.rbkmoney.fraudbusters.fraud.aggragator;

import com.rbkmoney.fraudbusters.repository.FraudResultRepository;
import com.rbkmoney.fraudbusters.util.TimestampUtil;
import com.rbkmoney.fraudo.aggregator.CountAggregator;
import com.rbkmoney.fraudo.constant.CheckedField;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;

@Slf4j
@RequiredArgsConstructor
public class CountAggregatorImpl implements CountAggregator {

    private final FraudResultRepository fraudResultRepository;

    @Override
    public Integer count(CheckedField checkedField, String s, Long aLong) {
        Instant now = Instant.now();
        Integer count = fraudResultRepository.countOperationByEmail(s, TimestampUtil.generateTimestampMinusMinutes(now, aLong),
                TimestampUtil.generateTimestampNow(now));
        log.debug("CountAggregatorImpl count: {}", count);
        return count;
    }

    @Override
    public Integer countSuccess(CheckedField checkedField, String s, Long aLong) {
        Instant now = Instant.now();
        return fraudResultRepository.countOperationByEmailSuccess(s, TimestampUtil.generateTimestampNow(now),
                TimestampUtil.generateTimestampMinusMinutes(now, aLong));
    }

    @Override
    public Integer countError(CheckedField checkedField, String s, Long aLong, String s1) {
        Instant now = Instant.now();
        return fraudResultRepository.countOperationByEmailError(s, TimestampUtil.generateTimestampNow(now),
                TimestampUtil.generateTimestampMinusMinutes(now, aLong));
    }
}
