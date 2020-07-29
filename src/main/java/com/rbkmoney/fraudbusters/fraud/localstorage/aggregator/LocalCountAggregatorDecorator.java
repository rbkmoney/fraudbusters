package com.rbkmoney.fraudbusters.fraud.localstorage.aggregator;

import com.rbkmoney.fraudbusters.aspect.BasicMetric;
import com.rbkmoney.fraudbusters.exception.RuleFunctionException;
import com.rbkmoney.fraudbusters.fraud.AggregateGroupingFunction;
import com.rbkmoney.fraudbusters.fraud.constant.PaymentCheckedField;
import com.rbkmoney.fraudbusters.fraud.localstorage.LocalResultStorageRepository;
import com.rbkmoney.fraudbusters.fraud.model.FieldModel;
import com.rbkmoney.fraudbusters.fraud.model.PaymentModel;
import com.rbkmoney.fraudbusters.fraud.payment.aggregator.CountAggregatorImpl;
import com.rbkmoney.fraudbusters.fraud.payment.resolver.DBPaymentFieldResolver;
import com.rbkmoney.fraudbusters.util.TimestampUtil;
import com.rbkmoney.fraudo.model.TimeWindow;
import com.rbkmoney.fraudo.payment.aggregator.CountPaymentAggregator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.validation.constraints.NotNull;
import java.time.Instant;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class LocalCountAggregatorDecorator implements CountPaymentAggregator<PaymentModel, PaymentCheckedField> {

    private final CountAggregatorImpl countAggregator;
    private final DBPaymentFieldResolver dbPaymentFieldResolver;
    private final LocalResultStorageRepository localStorageRepository;

    @Override
    public Integer count(PaymentCheckedField checkedField, PaymentModel paymentModel, TimeWindow timeWindow, List<PaymentCheckedField> list) {
        Integer count = countAggregator.count(checkedField, paymentModel, timeWindow, list);
        FieldModel resolve = dbPaymentFieldResolver.resolve(checkedField, paymentModel);
        Instant now = Instant.ofEpochMilli(paymentModel.getTimestamp());
        Instant instantFrom = Instant.ofEpochMilli(TimestampUtil.generateTimestampMinusMinutesMillis(now, timeWindow.getStartWindowTime()));
        Instant instantTo = Instant.ofEpochMilli(TimestampUtil.generateTimestampMinusMinutesMillis(now, timeWindow.getEndWindowTime()));
        Integer localCount = localStorageRepository.countOperationByField(checkedField.name(), resolve.getValue(),
                instantFrom.getEpochSecond(),
                instantTo.getEpochSecond());
        return localCount + count;
    }

    @Override
    public Integer countSuccess(PaymentCheckedField checkedField, PaymentModel paymentModel, TimeWindow timeWindow, List<PaymentCheckedField> list) {
        Integer countError = countAggregator.countSuccess(checkedField, paymentModel, timeWindow, list);
        Integer resultCount = getCount(checkedField, paymentModel, timeWindow, list, localStorageRepository::countOperationSuccessWithGroupBy)
                + countError;
        log.debug("LocalStorageCountAggregatorImpl resultCount: {}", resultCount);
        return resultCount;
    }

    @Override
    public Integer countError(PaymentCheckedField checkedField, PaymentModel paymentModel, TimeWindow timeWindow,
                              String errorCode, List<PaymentCheckedField> list) {
        try {
            Integer countError = countAggregator.countError(checkedField, paymentModel, timeWindow, errorCode, list);
            Instant now = Instant.ofEpochMilli(paymentModel.getTimestamp());
            FieldModel resolve = dbPaymentFieldResolver.resolve(checkedField, paymentModel);
            List<FieldModel> eventFields = dbPaymentFieldResolver.resolveListFields(paymentModel, list);
            Instant instantFrom = Instant.ofEpochMilli(TimestampUtil.generateTimestampMinusMinutesMillis(now, timeWindow.getStartWindowTime()));
            Instant instantTo = Instant.ofEpochMilli(TimestampUtil.generateTimestampMinusMinutesMillis(now, timeWindow.getEndWindowTime()));
            Integer localCount = localStorageRepository.countOperationErrorWithGroupBy(checkedField.name(), resolve.getValue(),
                    instantFrom.getEpochSecond(),
                    instantTo.getEpochSecond(),
                    eventFields, errorCode);
            int result = localCount + countError;
            log.debug("LocalStorageCountAggregatorImpl field: {} value: {}  countError: {}", resolve.getName(), resolve.getValue(), result);
            return result;
        } catch (Exception e) {
            log.warn("LocalStorageCountAggregatorImpl error when countError e: ", e);
            throw new RuleFunctionException(e);
        }
    }

    @Override
    public Integer countChargeback(PaymentCheckedField checkedField, PaymentModel model, TimeWindow timeWindow, List<PaymentCheckedField> fields) {
        return countAggregator.countChargeback(checkedField, model, timeWindow, fields);
    }

    @Override
    public Integer countRefund(PaymentCheckedField checkedField, PaymentModel model, TimeWindow timeWindow, List<PaymentCheckedField> fields) {
        return countAggregator.countRefund(checkedField, model, timeWindow, fields);
    }

    @NotNull
    private Integer getCount(PaymentCheckedField checkedField, PaymentModel paymentModel, TimeWindow timeWindow, List<PaymentCheckedField> list,
                             AggregateGroupingFunction<String, String, Long, Long, List<FieldModel>, Integer> aggregateFunction) {
        try {
            Instant now = paymentModel.getTimestamp() != null ? Instant.ofEpochMilli(paymentModel.getTimestamp()) : Instant.now();
            FieldModel resolve = dbPaymentFieldResolver.resolve(checkedField, paymentModel);
            List<FieldModel> eventFields = dbPaymentFieldResolver.resolveListFields(paymentModel, list);
            Integer count = aggregateFunction.accept(resolve.getName(), resolve.getValue(),
                    TimestampUtil.generateTimestampMinusMinutesMillis(now, timeWindow.getStartWindowTime()),
                    TimestampUtil.generateTimestampMinusMinutesMillis(now, timeWindow.getEndWindowTime()), eventFields);
            log.debug("LocalStorageCountAggregatorImpl field: {} value: {}  count: {}", resolve.getName(), resolve.getValue(), count);
            return count;
        } catch (Exception e) {
            log.warn("LocalStorageCountAggregatorImpl error when getCount e: ", e);
            throw new RuleFunctionException(e);
        }
    }
}
