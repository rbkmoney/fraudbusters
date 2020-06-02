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
import com.rbkmoney.fraudo.aggregator.SumAggregator;
import com.rbkmoney.fraudo.model.TimeWindow;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class SumP2PAggregatorImpl implements SumAggregator<P2PModel, P2PCheckedField> {

    private final AggregationRepository eventP2PRepository;
    private final DbP2pFieldResolver dbPaymentFieldResolver;

    @Override
    public Double sum(P2PCheckedField checkedField, P2PModel p2pModel, TimeWindow timeWindow, List<P2PCheckedField> list) {
        return getSum(checkedField, p2pModel, timeWindow, list, eventP2PRepository::sumOperationByFieldWithGroupBy);
    }

    @Override
    public Double sumSuccess(P2PCheckedField checkedField, P2PModel p2pModel, TimeWindow timeWindow, List<P2PCheckedField> list) {
        throw new UnsupportedOperationException("P2p is not support this operation!");
    }

    @Override
    public Double sumError(P2PCheckedField checkedField, P2PModel p2pModel, TimeWindow timeWindow, String errorCode, List<P2PCheckedField> list) {
        throw new UnsupportedOperationException("P2p is not support this operation!");
    }

    @Override
    public Double sumChargeback(P2PCheckedField p2PCheckedField, P2PModel p2PModel, TimeWindow timeWindow, List<P2PCheckedField> list) {
        throw new UnsupportedOperationException("P2p is not support this operation!");
    }

    @Override
    public Double sumRefund(P2PCheckedField p2PCheckedField, P2PModel p2PModel, TimeWindow timeWindow, List<P2PCheckedField> list) {
        throw new UnsupportedOperationException("P2p is not support this operation!");
    }

    @NotNull
    @BasicMetric(value = "getSumWindowed", extraTags = "p2p")
    private Double getSum(P2PCheckedField checkedField, P2PModel p2pModel, TimeWindow timeWindow, List<P2PCheckedField> list,
                          AggregateGroupingFunction<String, String, Long, Long, List<FieldModel>, Long> aggregateFunction) {
        try {
            Instant now = Instant.now();
            FieldModel resolve = dbPaymentFieldResolver.resolve(checkedField, p2pModel);
            List<FieldModel> eventFields = dbPaymentFieldResolver.resolveListFields(p2pModel, list);
            Long sum = aggregateFunction.accept(resolve.getName(), resolve.getValue(),
                    TimestampUtil.generateTimestampMinusMinutesMillis(now, timeWindow.getStartWindowTime()),
                    TimestampUtil.generateTimestampMinusMinutesMillis(now, timeWindow.getEndWindowTime()),
                    eventFields);
            double resultSum = (double) checkedLong(sum) + checkedLong(p2pModel.getAmount());
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
