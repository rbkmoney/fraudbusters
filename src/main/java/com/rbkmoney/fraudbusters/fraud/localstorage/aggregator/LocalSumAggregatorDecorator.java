package com.rbkmoney.fraudbusters.fraud.localstorage.aggregator;

import com.rbkmoney.fraudbusters.aspect.BasicMetric;
import com.rbkmoney.fraudbusters.exception.RuleFunctionException;
import com.rbkmoney.fraudbusters.fraud.AggregateGroupingFunction;
import com.rbkmoney.fraudbusters.fraud.constant.PaymentCheckedField;
import com.rbkmoney.fraudbusters.fraud.localstorage.LocalResultStorageRepository;
import com.rbkmoney.fraudbusters.fraud.model.FieldModel;
import com.rbkmoney.fraudbusters.fraud.model.PaymentModel;
import com.rbkmoney.fraudbusters.fraud.payment.aggregator.SumAggregatorImpl;
import com.rbkmoney.fraudbusters.fraud.payment.resolver.DBPaymentFieldResolver;
import com.rbkmoney.fraudbusters.util.TimestampUtil;
import com.rbkmoney.fraudo.model.TimeWindow;
import com.rbkmoney.fraudo.payment.aggregator.SumPaymentAggregator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class LocalSumAggregatorDecorator implements SumPaymentAggregator<PaymentModel, PaymentCheckedField> {

    private final SumAggregatorImpl sumAggregatorImpl;
    private final DBPaymentFieldResolver dbPaymentFieldResolver;
    private final LocalResultStorageRepository localStorageRepository;

    @Override
    public Double sum(PaymentCheckedField checkedField, PaymentModel paymentModel, TimeWindow timeWindow, List<PaymentCheckedField> list) {
        Double sum = sumAggregatorImpl.sum(checkedField, paymentModel, timeWindow, list);
        FieldModel resolve = dbPaymentFieldResolver.resolve(checkedField, paymentModel);
        Instant now = TimestampUtil.instantFromPaymentModel(paymentModel);
        Instant instantFrom = Instant.ofEpochMilli(TimestampUtil.generateTimestampMinusMinutesMillis(now, timeWindow.getStartWindowTime()));
        Instant instantTo = Instant.ofEpochMilli(TimestampUtil.generateTimestampMinusMinutesMillis(now, timeWindow.getEndWindowTime()));
        List<FieldModel> eventFields = dbPaymentFieldResolver.resolveListFields(paymentModel, list);
        Long localSum = localStorageRepository.sumOperationByFieldWithGroupBy(checkedField.name(), resolve.getValue(),
                instantFrom.getEpochSecond(),
                instantTo.getEpochSecond(), eventFields);
        Double resultSum = checkedLong(localSum) + sum;
        log.debug("LocalSumAggregatorDecorator sum: {}", resultSum);
        return resultSum;
    }

    @Override
    public Double sumSuccess(PaymentCheckedField checkedField, PaymentModel paymentModel, TimeWindow timeWindow, List<PaymentCheckedField> list) {
        Double sumSuccess = sumAggregatorImpl.sumSuccess(checkedField, paymentModel, timeWindow, list);
        Double resultCount = getSum(checkedField, paymentModel, timeWindow, list, localStorageRepository::sumOperationSuccessWithGroupBy)
                + sumSuccess;
        log.debug("LocalSumAggregatorDecorator sumSuccess: {}", resultCount);
        return resultCount;
    }

    @Override
    public Double sumError(PaymentCheckedField checkedField, PaymentModel paymentModel, TimeWindow timeWindow, String errorCode,
                           List<PaymentCheckedField> list) {
        try {
            Double sumError = sumAggregatorImpl.sumError(checkedField, paymentModel, timeWindow, errorCode, list);
            Instant now = TimestampUtil.instantFromPaymentModel(paymentModel);
            FieldModel resolve = dbPaymentFieldResolver.resolve(checkedField, paymentModel);
            List<FieldModel> eventFields = dbPaymentFieldResolver.resolveListFields(paymentModel, list);
            Instant instantFrom = Instant.ofEpochMilli(TimestampUtil.generateTimestampMinusMinutesMillis(now, timeWindow.getStartWindowTime()));
            Instant instantTo = Instant.ofEpochMilli(TimestampUtil.generateTimestampMinusMinutesMillis(now, timeWindow.getEndWindowTime()));
            Long localSum = localStorageRepository.sumOperationErrorWithGroupBy(checkedField.name(), resolve.getValue(),
                    instantFrom.getEpochSecond(),
                    instantTo.getEpochSecond(),
                    eventFields, errorCode);
            Double result = checkedLong(localSum) + sumError;
            log.debug("LocalSumAggregatorDecorator field: {} value: {}  sumError: {}", resolve.getName(), resolve.getValue(), result);
            return result;
        } catch (Exception e) {
            log.warn("LocalSumAggregatorDecorator error when sumError e: ", e);
            throw new RuleFunctionException(e);
        }
    }

    @Override
    public Double sumChargeback(PaymentCheckedField checkedField, PaymentModel paymentModel, TimeWindow timeWindow, List<PaymentCheckedField> list) {
        return sumAggregatorImpl.sumChargeback(checkedField, paymentModel, timeWindow, list);
    }

    @Override
    public Double sumRefund(PaymentCheckedField checkedField, PaymentModel paymentModel, TimeWindow timeWindow, List<PaymentCheckedField> list) {
        return sumAggregatorImpl.sumRefund(checkedField, paymentModel, timeWindow, list);
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
            Instant now = TimestampUtil.instantFromPaymentModel(paymentModel);
            FieldModel resolve = dbPaymentFieldResolver.resolve(checkedField, paymentModel);
            List<FieldModel> eventFields = dbPaymentFieldResolver.resolveListFields(paymentModel, list);
            Long sum = aggregateFunction.accept(resolve.getName(), resolve.getValue(),
                    TimestampUtil.generateTimestampMinusMinutesMillis(now, timeWindow.getStartWindowTime()),
                    TimestampUtil.generateTimestampMinusMinutesMillis(now, timeWindow.getEndWindowTime()), eventFields);
            double resultSum = withCurrent ? (double) checkedLong(sum) + checkedLong(paymentModel.getAmount()) : checkedLong(sum);
            log.debug("LocalSumAggregatorDecorator field: {} value: {}  sum: {}", resolve.getName(), resolve.getValue(), resultSum);
            return resultSum;
        } catch (Exception e) {
            log.warn("LocalSumAggregatorDecorator error when getSum e: ", e);
            throw new RuleFunctionException(e);
        }
    }

    private Long checkedLong(Long entry) {
        return entry != null ? entry : 0L;
    }
}
