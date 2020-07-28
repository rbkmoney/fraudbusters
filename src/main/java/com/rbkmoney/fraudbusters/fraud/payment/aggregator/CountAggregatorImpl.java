package com.rbkmoney.fraudbusters.fraud.payment.aggregator;

import com.rbkmoney.fraudbusters.aspect.BasicMetric;
import com.rbkmoney.fraudbusters.exception.RuleFunctionException;
import com.rbkmoney.fraudbusters.fraud.AggregateGroupingFunction;
import com.rbkmoney.fraudbusters.fraud.constant.PaymentCheckedField;
import com.rbkmoney.fraudbusters.fraud.model.FieldModel;
import com.rbkmoney.fraudbusters.fraud.model.PaymentModel;
import com.rbkmoney.fraudbusters.fraud.payment.resolver.DBPaymentFieldResolver;
import com.rbkmoney.fraudbusters.repository.AggregationRepository;
import com.rbkmoney.fraudbusters.repository.PaymentRepository;
import com.rbkmoney.fraudbusters.util.TimestampUtil;
import com.rbkmoney.fraudo.model.TimeWindow;
import com.rbkmoney.fraudo.payment.aggregator.CountPaymentAggregator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class CountAggregatorImpl implements CountPaymentAggregator<PaymentModel, PaymentCheckedField> {

    public static final int CURRENT_ONE = 1;

    private final DBPaymentFieldResolver dbPaymentFieldResolver;
    private final PaymentRepository paymentRepository;
    private final AggregationRepository analyticsRefundRepository;
    private final AggregationRepository analyticsChargebackRepository;

    @Override
    @BasicMetric("count")
    public Integer count(PaymentCheckedField checkedField, PaymentModel paymentModel, TimeWindow timeWindow, List<PaymentCheckedField> list) {
        return getCount(checkedField, paymentModel, timeWindow, list, paymentRepository::countOperationByFieldWithGroupBy);
    }

    @Override
    @BasicMetric("countSuccess")
    public Integer countSuccess(PaymentCheckedField checkedField, PaymentModel paymentModel, TimeWindow timeWindow, List<PaymentCheckedField> list) {
        return getCount(checkedField, paymentModel, timeWindow, list, paymentRepository::countOperationSuccessWithGroupBy);
    }

    @Override
    @BasicMetric("countError")
    public Integer countError(PaymentCheckedField checkedField, PaymentModel paymentModel, TimeWindow timeWindow,
                              String errorCode, List<PaymentCheckedField> list) {
        try {
            Instant now = Instant.now();
            FieldModel resolve = dbPaymentFieldResolver.resolve(checkedField, paymentModel);
            List<FieldModel> eventFields = dbPaymentFieldResolver.resolveListFields(paymentModel, list);
            if (StringUtils.isEmpty(resolve.getValue())) {
                return CURRENT_ONE;
            }
            Integer count = paymentRepository.countOperationErrorWithGroupBy(resolve.getName(), resolve.getValue(),
                    TimestampUtil.generateTimestampMinusMinutesMillis(now, timeWindow.getStartWindowTime()),
                    TimestampUtil.generateTimestampMinusMinutesMillis(now, timeWindow.getEndWindowTime()),
                    eventFields, errorCode);

            log.debug("CountAggregatorImpl field: {} value: {}  countError: {}", resolve.getName(), resolve.getValue(), count);
            return count + CURRENT_ONE;
        } catch (Exception e) {
            log.warn("CountAggregatorImpl error when countError e: ", e);
            throw new RuleFunctionException(e);
        }
    }

    @Override
    @BasicMetric("countChargeback")
    public Integer countChargeback(PaymentCheckedField paymentCheckedField, PaymentModel paymentModel, TimeWindow timeWindow, List<PaymentCheckedField> list) {
        return getCount(paymentCheckedField, paymentModel, timeWindow, list, analyticsChargebackRepository::countOperationByFieldWithGroupBy, false);
    }

    @Override
    @BasicMetric("countRefund")
    public Integer countRefund(PaymentCheckedField paymentCheckedField, PaymentModel paymentModel, TimeWindow timeWindow, List<PaymentCheckedField> list) {
        return getCount(paymentCheckedField, paymentModel, timeWindow, list, analyticsRefundRepository::countOperationByFieldWithGroupBy, false);
    }

    @NotNull
    private Integer getCount(PaymentCheckedField checkedField, PaymentModel paymentModel, TimeWindow timeWindow, List<PaymentCheckedField> list,
                             AggregateGroupingFunction<String, String, Long, Long, List<FieldModel>, Integer> aggregateFunction) {
        return getCount(checkedField, paymentModel, timeWindow, list, aggregateFunction, true);
    }

    @NotNull
    private Integer getCount(PaymentCheckedField checkedField, PaymentModel paymentModel, TimeWindow timeWindow, List<PaymentCheckedField> list,
                             AggregateGroupingFunction<String, String, Long, Long, List<FieldModel>, Integer> aggregateFunction, boolean withCurrent) {
        try {
            Instant now = paymentModel.getTimestamp() != null ? Instant.ofEpochMilli(paymentModel.getTimestamp()) : Instant.now();
            FieldModel resolve = dbPaymentFieldResolver.resolve(checkedField, paymentModel);
            List<FieldModel> eventFields = dbPaymentFieldResolver.resolveListFields(paymentModel, list);

            if (StringUtils.isEmpty(resolve.getValue())) {
                return withCurrent ? CURRENT_ONE : 0;
            }

            Integer count = aggregateFunction.accept(resolve.getName(), resolve.getValue(),
                    TimestampUtil.generateTimestampMinusMinutesMillis(now, timeWindow.getStartWindowTime()),
                    TimestampUtil.generateTimestampMinusMinutesMillis(now, timeWindow.getEndWindowTime()), eventFields);

            log.debug("CountAggregatorImpl field: {} value: {}  count: {}", resolve.getName(), resolve.getValue(), count);
            return withCurrent ? count + CURRENT_ONE : count;
        } catch (Exception e) {
            log.warn("CountAggregatorImpl error when getCount e: ", e);
            throw new RuleFunctionException(e);
        }
    }
}
