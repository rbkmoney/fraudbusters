package com.rbkmoney.fraudbusters.fraud.p2p.aggregator;

import com.rbkmoney.fraudbusters.aspect.BasicMetric;
import com.rbkmoney.fraudbusters.exception.RuleFunctionException;
import com.rbkmoney.fraudbusters.fraud.AggregateGroupingFunction;
import com.rbkmoney.fraudbusters.fraud.constant.P2PCheckedField;
import com.rbkmoney.fraudbusters.fraud.model.FieldModel;
import com.rbkmoney.fraudbusters.fraud.model.P2PModel;
import com.rbkmoney.fraudbusters.fraud.p2p.resolver.DbP2pFieldResolver;
import com.rbkmoney.fraudbusters.repository.AggregationRepository;
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
    private final AggregationRepository eventP2PRepository;
    private final DbP2pFieldResolver dbP2pFieldResolver;

    @Override
    @BasicMetric(value = "count", extraTags = "p2p")
    public Integer count(P2PCheckedField checkedField, P2PModel p2pModel, TimeWindow timeWindow, List<P2PCheckedField> list) {
        return getCount(checkedField, p2pModel, timeWindow, list, eventP2PRepository::countOperationByFieldWithGroupBy);
    }

    @Override
    @BasicMetric(value = "countSuccess", extraTags = "p2p")
    public Integer countSuccess(P2PCheckedField checkedField, P2PModel p2pModel, TimeWindow timeWindow, List<P2PCheckedField> list) {
        return getCount(checkedField, p2pModel, timeWindow, list, eventP2PRepository::countOperationSuccessWithGroupBy);
    }

    @Override
    @BasicMetric(value = "countError", extraTags = "p2p")
    public Integer countError(P2PCheckedField checkedField, P2PModel p2pModel, TimeWindow timeWindow, String errorCode, List<P2PCheckedField> list) {
        try {
            Instant now = Instant.now();
            FieldModel resolve = dbP2pFieldResolver.resolve(checkedField, p2pModel);
            List<FieldModel> eventFields = dbP2pFieldResolver.resolveListFields(p2pModel, list);

            Integer count = eventP2PRepository.countOperationErrorWithGroupBy(resolve.getName(), resolve.getValue(),
                    TimestampUtil.generateTimestampMinusMinutesMillis(now, timeWindow.getStartWindowTime()),
                    TimestampUtil.generateTimestampMinusMinutesMillis(now, timeWindow.getEndWindowTime()),
                    eventFields, errorCode);

            log.debug("CountAggregatorImpl field: {} value: {}  count: {}", resolve.getName(), resolve.getValue(), count);
            return count + CURRENT_ONE;
        } catch (Exception e) {
            log.warn("CountAggregatorImpl error when getCount e: ", e);
            throw new RuleFunctionException(e);
        }
    }

    @NotNull
    private Integer getCount(P2PCheckedField checkedField, P2PModel p2pModel, TimeWindow timeWindow, List<P2PCheckedField> list,
                             AggregateGroupingFunction<String, String, Long, Long, List<FieldModel>, Integer> aggregateFunction) {
        try {
            Instant now = Instant.now();
            FieldModel resolve = dbP2pFieldResolver.resolve(checkedField, p2pModel);
            List<FieldModel> eventFields = dbP2pFieldResolver.resolveListFields(p2pModel, list);

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
