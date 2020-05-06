package com.rbkmoney.fraudbusters.fraud.payment.aggregator;

import com.rbkmoney.fraudbusters.aspect.BasicMetric;
import com.rbkmoney.fraudbusters.exception.RuleFunctionException;
import com.rbkmoney.fraudbusters.fraud.AggregateGroupingFunction;
import com.rbkmoney.fraudbusters.fraud.constant.PaymentCheckedField;
import com.rbkmoney.fraudbusters.fraud.model.FieldModel;
import com.rbkmoney.fraudbusters.fraud.model.PaymentModel;
import com.rbkmoney.fraudbusters.fraud.payment.resolver.DBPaymentFieldResolver;
import com.rbkmoney.fraudbusters.repository.AggregationRepository;
import com.rbkmoney.fraudbusters.repository.source.SourcePool;
import com.rbkmoney.fraudbusters.util.TimestampUtil;
import com.rbkmoney.fraudo.aggregator.SumAggregator;
import com.rbkmoney.fraudo.model.TimeWindow;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class SumAggregatorImpl implements SumAggregator<PaymentModel, PaymentCheckedField> {

    private final DBPaymentFieldResolver dbPaymentFieldResolver;
    private final SourcePool sourcePool;

    @Override
    public Double sum(PaymentCheckedField checkedField, PaymentModel paymentModel, TimeWindow timeWindow, List<PaymentCheckedField> list) {
        AggregationRepository activeSource = sourcePool.getActiveSource();
        return getSum(checkedField, paymentModel, timeWindow, list, activeSource::sumOperationByFieldWithGroupBy);
    }

    @Override
    public Double sumSuccess(PaymentCheckedField checkedField, PaymentModel paymentModel, TimeWindow timeWindow, List<PaymentCheckedField> list) {
        AggregationRepository activeSource = sourcePool.getActiveSource();
        return getSum(checkedField, paymentModel, timeWindow, list, activeSource::sumOperationSuccessWithGroupBy);
    }

    @Override
    public Double sumError(PaymentCheckedField checkedField, PaymentModel paymentModel, TimeWindow timeWindow, String s, List<PaymentCheckedField> list) {
        try {
            Instant now = Instant.now();
            FieldModel resolve = dbPaymentFieldResolver.resolve(checkedField, paymentModel);
            if (StringUtils.isEmpty(resolve.getValue())) {
                return Double.valueOf(checkedLong(paymentModel.getAmount()));
            }
            List<FieldModel> eventFields = dbPaymentFieldResolver.resolveListFields(paymentModel, list);
            AggregationRepository activeSource = sourcePool.getActiveSource();
            Long sum = activeSource.sumOperationErrorWithGroupBy(resolve.getName(), resolve.getValue(),
                    TimestampUtil.generateTimestampMinusMinutesMillis(now, timeWindow.getStartWindowTime()),
                    TimestampUtil.generateTimestampMinusMinutesMillis(now, timeWindow.getEndWindowTime()), eventFields);
            double resultSum = (double) checkedLong(sum) + checkedLong(paymentModel.getAmount());
            log.debug("SumAggregatorImpl field: {} value: {}  sumError: {}", resolve.getName(), resolve.getValue(), resultSum);
            return resultSum;
        } catch (Exception e) {
            log.warn("SumAggregatorImpl error when sumError e: ", e);
            throw new RuleFunctionException(e);
        }
    }

    @NotNull
    @BasicMetric("getSumWindowed")
    private Double getSum(PaymentCheckedField checkedField, PaymentModel paymentModel, TimeWindow timeWindow, List<PaymentCheckedField> list,
                          AggregateGroupingFunction<String, String, Long, Long, List<FieldModel>, Long> aggregateFunction) {
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
            double resultSum = (double) checkedLong(sum) + checkedLong(paymentModel.getAmount());
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
