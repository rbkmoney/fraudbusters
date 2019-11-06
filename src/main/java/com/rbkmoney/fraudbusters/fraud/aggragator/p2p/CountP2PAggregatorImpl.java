package com.rbkmoney.fraudbusters.fraud.aggragator.p2p;

import com.rbkmoney.fraudbusters.aspect.BasicMetric;
import com.rbkmoney.fraudbusters.exception.RuleFunctionException;
import com.rbkmoney.fraudbusters.fraud.aggragator.AggregateGroupingFunction;
import com.rbkmoney.fraudbusters.fraud.constant.P2PCheckedField;
import com.rbkmoney.fraudbusters.fraud.model.P2PModel;
import com.rbkmoney.fraudbusters.fraud.resolver.DbP2pFieldResolver;
import com.rbkmoney.fraudbusters.fraud.resolver.FieldModel;
import com.rbkmoney.fraudbusters.repository.EventP2PRepository;
import com.rbkmoney.fraudbusters.repository.MgEventSinkRepository;
import com.rbkmoney.fraudbusters.util.TimestampUtil;
import com.rbkmoney.fraudo.aggregator.CountAggregator;
import com.rbkmoney.fraudo.model.TimeWindow;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class CountP2PAggregatorImpl implements CountAggregator<P2PModel, P2PCheckedField> {

    private static final int CURRENT_ONE = 1;
    private final EventP2PRepository eventP2PRepository;
    private final MgEventSinkRepository mgEventSinkRepository;
    private final DbP2pFieldResolver dbP2pFieldResolver;

    @Override
    @BasicMetric("count")
    public Integer count(P2PCheckedField checkedField, P2PModel fraudModel, TimeWindow timeWindow, List<P2PCheckedField> list) {
        return getCount(checkedField, fraudModel, timeWindow, list, eventP2PRepository::countOperationByFieldWithGroupBy);
    }

    @Override
    @BasicMetric("countSuccess")
    public Integer countSuccess(P2PCheckedField checkedField, P2PModel fraudModel, TimeWindow timeWindow, List<P2PCheckedField> list) {
        return getCount(checkedField, fraudModel, timeWindow, list, mgEventSinkRepository::countOperationSuccessWithGroupBy);
    }

    @Override
    @BasicMetric("countError")
    public Integer countError(P2PCheckedField checkedField, P2PModel fraudModel, TimeWindow timeWindow, String s, List<P2PCheckedField> list) {
        return getCount(checkedField, fraudModel, timeWindow, list, mgEventSinkRepository::countOperationErrorWithGroupBy);
    }

    @NotNull
    private Integer getCount(P2PCheckedField checkedField, P2PModel fraudModel, TimeWindow timeWindow, List<P2PCheckedField> list,
                             AggregateGroupingFunction<String, String, Long, Long, List<FieldModel>, Integer> aggregateFunction) {
        try {
            Instant now = Instant.now();
            FieldModel resolve = dbP2pFieldResolver.resolve(checkedField, fraudModel);
            List<FieldModel> eventFields = dbP2pFieldResolver.resolveListFields(fraudModel, list);

            Integer count = aggregateFunction.accept(resolve.getName(), resolve.getValue(),
                    TimestampUtil.generateTimestampMinusMinutesMillis(now, timeWindow.getStartWindowTime()),
                    TimestampUtil.generateTimestampMinusMinutesMillis(now, timeWindow.getEndWindowTime()),
                    eventFields);

            log.debug("CountAggregatorImpl field: {} value: {}  count: {}", resolve.getName(), resolve.getValue(), count);
            return count + CURRENT_ONE;
        } catch (Exception e) {
            log.warn("CountAggregatorImpl error when getCount e: ", e);
            throw new RuleFunctionException(e);
        }
    }
}
