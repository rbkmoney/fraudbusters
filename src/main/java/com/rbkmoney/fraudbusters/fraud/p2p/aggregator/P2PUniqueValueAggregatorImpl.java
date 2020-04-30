package com.rbkmoney.fraudbusters.fraud.p2p.aggregator;

import com.rbkmoney.fraudbusters.aspect.BasicMetric;
import com.rbkmoney.fraudbusters.exception.RuleFunctionException;
import com.rbkmoney.fraudbusters.fraud.constant.P2PCheckedField;
import com.rbkmoney.fraudbusters.fraud.model.FieldModel;
import com.rbkmoney.fraudbusters.fraud.model.P2PModel;
import com.rbkmoney.fraudbusters.fraud.p2p.resolver.DbP2pFieldResolver;
import com.rbkmoney.fraudbusters.repository.AggregationRepository;
import com.rbkmoney.fraudbusters.repository.impl.EventP2PRepository;
import com.rbkmoney.fraudbusters.util.TimestampUtil;
import com.rbkmoney.fraudo.aggregator.UniqueValueAggregator;
import com.rbkmoney.fraudo.model.TimeWindow;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class P2PUniqueValueAggregatorImpl implements UniqueValueAggregator<P2PModel, P2PCheckedField> {

    private static final int CURRENT_ONE = 1;
    private final AggregationRepository eventP2PRepository;
    private final DbP2pFieldResolver dbP2pFieldResolver;

    @Override
    @BasicMetric(value = "countUniqueValueWindowed", extraTags = "p2p")
    public Integer countUniqueValue(P2PCheckedField countField,
                                    P2PModel payoutModel,
                                    P2PCheckedField onField,
                                    TimeWindow timeWindow,
                                    List<P2PCheckedField> list) {
        try {
            Instant now = Instant.now();
            FieldModel resolve = dbP2pFieldResolver.resolve(countField, payoutModel);
            List<FieldModel> fieldModels = dbP2pFieldResolver.resolveListFields(payoutModel, list);
            Integer uniqCountOperation = eventP2PRepository.uniqCountOperationWithGroupBy(resolve.getName(), resolve.getValue(),
                    dbP2pFieldResolver.resolve(onField),
                    TimestampUtil.generateTimestampMinusMinutesMillis(now, timeWindow.getStartWindowTime()),
                    TimestampUtil.generateTimestampMinusMinutesMillis(now, timeWindow.getEndWindowTime()),
                    fieldModels);
            return uniqCountOperation + CURRENT_ONE;
        } catch (Exception e) {
            log.warn("UniqueValueAggregatorImpl error when getCount e: ", e);
            throw new RuleFunctionException(e);
        }
    }
}
