package com.rbkmoney.fraudbusters.fraud.payment.aggregator;

import com.rbkmoney.fraudbusters.aspect.BasicMetric;
import com.rbkmoney.fraudbusters.exception.RuleFunctionException;
import com.rbkmoney.fraudbusters.fraud.AggregateGroupingFunction;
import com.rbkmoney.fraudbusters.fraud.constant.PaymentCheckedField;
import com.rbkmoney.fraudbusters.fraud.model.FieldModel;
import com.rbkmoney.fraudbusters.fraud.model.PaymentModel;
import com.rbkmoney.fraudbusters.fraud.payment.resolver.DBPaymentFieldResolver;
import com.rbkmoney.fraudbusters.repository.EventRepository;
import com.rbkmoney.fraudbusters.repository.MgEventSinkRepository;
import com.rbkmoney.fraudbusters.util.TimestampUtil;
import com.rbkmoney.fraudo.aggregator.CountAggregator;
import com.rbkmoney.fraudo.model.TimeWindow;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class CountAggregatorImpl implements CountAggregator<PaymentModel, PaymentCheckedField> {

    private static final int CURRENT_ONE = 1;
    private final EventRepository eventRepository;
    private final MgEventSinkRepository mgEventSinkRepository;
    private final DBPaymentFieldResolver dbPaymentFieldResolver;

    @Override
    @BasicMetric("count")
    public Integer count(PaymentCheckedField checkedField, PaymentModel paymentModel, TimeWindow timeWindow, List<PaymentCheckedField> list) {
        return getCount(checkedField, paymentModel, timeWindow, list, eventRepository::countOperationByFieldWithGroupBy);
    }

    @Override
    @BasicMetric("countSuccess")
    public Integer countSuccess(PaymentCheckedField checkedField, PaymentModel paymentModel, TimeWindow timeWindow, List<PaymentCheckedField> list) {
        return getCount(checkedField, paymentModel, timeWindow, list, mgEventSinkRepository::countOperationSuccessWithGroupBy);
    }

    @Override
    @BasicMetric("countError")
    public Integer countError(PaymentCheckedField checkedField, PaymentModel paymentModel, TimeWindow timeWindow, String s, List<PaymentCheckedField> list) {
        return getCount(checkedField, paymentModel, timeWindow, list, mgEventSinkRepository::countOperationErrorWithGroupBy);
    }

    @NotNull
    private Integer getCount(PaymentCheckedField checkedField, PaymentModel paymentModel, TimeWindow timeWindow, List<PaymentCheckedField> list,
                             AggregateGroupingFunction<String, String, Long, Long, List<FieldModel>, Integer> aggregateFunction) {
        try {
            Instant now = Instant.now();
            FieldModel resolve = dbPaymentFieldResolver.resolve(checkedField, paymentModel);
            List<FieldModel> eventFields = dbPaymentFieldResolver.resolveListFields(paymentModel, list);

            if (StringUtils.isEmpty(resolve.getValue())) {
                return CURRENT_ONE;
            }

            Integer count = aggregateFunction.accept(resolve.getName(), resolve.getValue(),
                    TimestampUtil.generateTimestampMinusMinutes(now, timeWindow.getStartWindowTime()),
                    TimestampUtil.generateTimestampMinusMinutes(now, timeWindow.getEndWindowTime()),
                    eventFields);

            log.debug("CountAggregatorImpl field: {} value: {}  count: {}", resolve.getName(), resolve.getValue(), count);
            return count + CURRENT_ONE;
        } catch (Exception e) {
            log.warn("CountAggregatorImpl error when getCount e: ", e);
            throw new RuleFunctionException(e);
        }
    }
}
