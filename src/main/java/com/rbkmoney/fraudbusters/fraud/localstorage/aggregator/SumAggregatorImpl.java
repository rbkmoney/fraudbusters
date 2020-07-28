package com.rbkmoney.fraudbusters.fraud.localstorage.aggregator;

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
import com.rbkmoney.fraudo.payment.aggregator.SumPaymentAggregator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class SumAggregatorImpl implements SumPaymentAggregator<PaymentModel, PaymentCheckedField> {

    private final DBPaymentFieldResolver dbPaymentFieldResolver;
    private final PaymentRepository paymentRepository;
    private final AggregationRepository refundRepository;
    private final AggregationRepository chargebackRepository;

    @Override
    public Double sum(PaymentCheckedField checkedField, PaymentModel paymentModel, TimeWindow timeWindow, List<PaymentCheckedField> list) {
        return getSum(checkedField, paymentModel, timeWindow, list, paymentRepository::sumOperationByFieldWithGroupBy);
    }

    @Override
    public Double sumSuccess(PaymentCheckedField checkedField, PaymentModel paymentModel, TimeWindow timeWindow, List<PaymentCheckedField> list) {
        return getSum(checkedField, paymentModel, timeWindow, list, paymentRepository::sumOperationSuccessWithGroupBy);
    }

    @Override
    public Double sumError(PaymentCheckedField checkedField, PaymentModel paymentModel, TimeWindow timeWindow, String errorCode,
                           List<PaymentCheckedField> list) {
        try {
            Instant now = Instant.now();
            FieldModel resolve = dbPaymentFieldResolver.resolve(checkedField, paymentModel);
            if (StringUtils.isEmpty(resolve.getValue())) {
                return Double.valueOf(checkedLong(paymentModel.getAmount()));
            }
            List<FieldModel> eventFields = dbPaymentFieldResolver.resolveListFields(paymentModel, list);
            Long sum = paymentRepository.sumOperationErrorWithGroupBy(resolve.getName(), resolve.getValue(),
                    TimestampUtil.generateTimestampMinusMinutesMillis(now, timeWindow.getStartWindowTime()),
                    TimestampUtil.generateTimestampMinusMinutesMillis(now, timeWindow.getEndWindowTime()), eventFields, errorCode);
            double resultSum = (double) checkedLong(sum) + checkedLong(paymentModel.getAmount());
            log.debug("SumAggregatorImpl field: {} value: {}  sumError: {}", resolve.getName(), resolve.getValue(), resultSum);
            return resultSum;
        } catch (Exception e) {
            log.warn("SumAggregatorImpl error when sumError e: ", e);
            throw new RuleFunctionException(e);
        }
    }

    @Override
    public Double sumChargeback(PaymentCheckedField checkedField, PaymentModel paymentModel, TimeWindow timeWindow, List<PaymentCheckedField> list) {
        return getSum(checkedField, paymentModel, timeWindow, list, chargebackRepository::sumOperationByFieldWithGroupBy, false);
    }

    @Override
    public Double sumRefund(PaymentCheckedField checkedField, PaymentModel paymentModel, TimeWindow timeWindow, List<PaymentCheckedField> list) {
        return getSum(checkedField, paymentModel, timeWindow, list, refundRepository::sumOperationByFieldWithGroupBy, false);
    }

    @NotNull
    private Double getSum(PaymentCheckedField checkedField, PaymentModel paymentModel, TimeWindow timeWindow, List<PaymentCheckedField> list,
                          AggregateGroupingFunction<String, String, Long, Long, List<FieldModel>, Long> aggregateFunction) {
        return getSum(checkedField, paymentModel, timeWindow, list, aggregateFunction, true);
    }

    @NotNull
    @BasicMetric("getSumWindowed")
    private Double getSum(PaymentCheckedField checkedField, PaymentModel paymentModel, TimeWindow timeWindow, List<PaymentCheckedField> list,
                          AggregateGroupingFunction<String, String, Long, Long, List<FieldModel>, Long> aggregateFunction, boolean withCurrent) {
        try {
            Instant now = Instant.now();
            FieldModel resolve = dbPaymentFieldResolver.resolve(checkedField, paymentModel);
            if (StringUtils.isEmpty(resolve.getValue())) {
                return Double.valueOf(checkedLong(paymentModel.getAmount()));
            }
            List<FieldModel> eventFields = dbPaymentFieldResolver.resolveListFields(paymentModel, list);
            Long sum = aggregateFunction.accept(resolve.getName(), resolve.getValue(),
                    TimestampUtil.generateTimestampMinusMinutesMillis(now, timeWindow.getStartWindowTime()),
                    TimestampUtil.generateTimestampMinusMinutesMillis(now, timeWindow.getEndWindowTime()), eventFields);
            double resultSum = withCurrent ? (double) checkedLong(sum) + checkedLong(paymentModel.getAmount()) : checkedLong(sum);
            log.debug("SumAggregatorImpl field: {} value: {}  sum: {}", resolve.getName(), resolve.getValue(), resultSum);
            return resultSum;
        } catch (Exception e) {
            log.warn("SumAggregatorImpl error when getSum e: ", e);
            throw new RuleFunctionException(e);
        }
    }

    private Long checkedLong(Long entry) {
        return entry != null ? entry : 0L;
    }
}
