package com.rbkmoney.fraudbusters.fraud.payment.aggregator;

import com.rbkmoney.fraudbusters.aspect.BasicMetric;
import com.rbkmoney.fraudbusters.exception.RuleFunctionException;
import com.rbkmoney.fraudbusters.fraud.AggregateGroupingFunction;
import com.rbkmoney.fraudbusters.fraud.constant.PaymentCheckedField;
import com.rbkmoney.fraudbusters.fraud.model.PaymentModel;
import com.rbkmoney.fraudbusters.fraud.payment.resolver.DBPaymentFieldResolver;
import com.rbkmoney.fraudbusters.fraud.model.FieldModel;
import com.rbkmoney.fraudbusters.repository.EventRepository;
import com.rbkmoney.fraudbusters.repository.MgEventSinkRepository;
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
public class SumAggregatorImpl implements SumAggregator<PaymentModel, PaymentCheckedField> {

    private final EventRepository eventRepository;
    private final MgEventSinkRepository mgEventSinkRepository;
    private final DBPaymentFieldResolver dbPaymentFieldResolver;

    @Override
    public Double sum(PaymentCheckedField checkedField, PaymentModel fraudModel, TimeWindow timeWindow, List<PaymentCheckedField> list) {
        return getSum(checkedField, fraudModel, timeWindow, list, eventRepository::sumOperationByFieldWithGroupBy);
    }

    @Override
    public Double sumSuccess(PaymentCheckedField checkedField, PaymentModel fraudModel, TimeWindow timeWindow, List<PaymentCheckedField> list) {
        return getSum(checkedField, fraudModel, timeWindow, list, mgEventSinkRepository::sumOperationSuccessWithGroupBy);
    }

    @Override
    public Double sumError(PaymentCheckedField checkedField, PaymentModel fraudModel, TimeWindow timeWindow, String s, List<PaymentCheckedField> list) {
        return getSum(checkedField, fraudModel, timeWindow, list, mgEventSinkRepository::sumOperationErrorWithGroupBy);
    }

    @NotNull
    @BasicMetric("getSumWindowed")
    private Double getSum(PaymentCheckedField checkedField, PaymentModel fraudModel, TimeWindow timeWindow, List<PaymentCheckedField> list,
                          AggregateGroupingFunction<String, String, Long, Long, List<FieldModel>, Long> aggregateFunction) {
        try {
            Instant now = Instant.now();
            FieldModel resolve = dbPaymentFieldResolver.resolve(checkedField, fraudModel);
            List<FieldModel> eventFields = dbPaymentFieldResolver.resolveListFields(fraudModel, list);
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
