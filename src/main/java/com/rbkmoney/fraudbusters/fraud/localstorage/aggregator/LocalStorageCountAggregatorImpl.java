package com.rbkmoney.fraudbusters.fraud.localstorage.aggregator;

import com.rbkmoney.fraudbusters.aspect.BasicMetric;
import com.rbkmoney.fraudbusters.fraud.constant.PaymentCheckedField;
import com.rbkmoney.fraudbusters.fraud.localstorage.LocalResultStorageRepository;
import com.rbkmoney.fraudbusters.fraud.model.FieldModel;
import com.rbkmoney.fraudbusters.fraud.model.PaymentModel;
import com.rbkmoney.fraudbusters.fraud.payment.aggregator.CountAggregatorImpl;
import com.rbkmoney.fraudbusters.fraud.payment.resolver.DBPaymentFieldResolver;
import com.rbkmoney.fraudbusters.repository.AggregationRepository;
import com.rbkmoney.fraudbusters.repository.PaymentRepository;
import com.rbkmoney.fraudbusters.util.TimestampUtil;
import com.rbkmoney.fraudo.model.TimeWindow;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;
import java.util.List;

@Slf4j
public class LocalStorageCountAggregatorImpl extends CountAggregatorImpl {

    private final DBPaymentFieldResolver dbPaymentFieldResolver;
    private final LocalResultStorageRepository localStorage;

    public LocalStorageCountAggregatorImpl(LocalResultStorageRepository localStorage, DBPaymentFieldResolver dbPaymentFieldResolver, PaymentRepository paymentRepository, AggregationRepository analyticsRefundRepository, AggregationRepository analyticsChargebackRepository) {
        super(dbPaymentFieldResolver, paymentRepository, analyticsRefundRepository, analyticsChargebackRepository);
        this.localStorage = localStorage;
        this.dbPaymentFieldResolver = dbPaymentFieldResolver;
    }

    @Override
    @BasicMetric("count")
    public Integer count(PaymentCheckedField checkedField, PaymentModel paymentModel, TimeWindow timeWindow, List<PaymentCheckedField> list) {
        Integer count = super.count(checkedField, paymentModel, timeWindow, list);
        FieldModel resolve = dbPaymentFieldResolver.resolve(checkedField, paymentModel);
        Instant now = Instant.now();
        Integer localCount = localStorage.countOperationByField(resolve.getName(), resolve.getValue(),
                TimestampUtil.generateTimestampMinusMinutesMillis(now, timeWindow.getStartWindowTime()),
                TimestampUtil.generateTimestampMinusMinutesMillis(now, timeWindow.getEndWindowTime()));
        return localCount + count;
    }
//
//    @Override
//    @BasicMetric("countSuccess")
//    public Integer countSuccess(PaymentCheckedField checkedField, PaymentModel paymentModel, TimeWindow timeWindow, List<PaymentCheckedField> list) {
//        return getCount(checkedField, paymentModel, timeWindow, list, paymentRepository::countOperationSuccessWithGroupBy);
//    }
//
//    @Override
//    @BasicMetric("countError")
//    public Integer countError(PaymentCheckedField checkedField, PaymentModel paymentModel, TimeWindow timeWindow,
//                              String errorCode, List<PaymentCheckedField> list) {
//        try {
//            Instant now = Instant.now();
//            FieldModel resolve = dbPaymentFieldResolver.resolve(checkedField, paymentModel);
//            List<FieldModel> eventFields = dbPaymentFieldResolver.resolveListFields(paymentModel, list);
//            if (StringUtils.isEmpty(resolve.getValue())) {
//                return CURRENT_ONE;
//            }
//            Integer count = paymentRepository.countOperationErrorWithGroupBy(resolve.getName(), resolve.getValue(),
//                    TimestampUtil.generateTimestampMinusMinutesMillis(now, timeWindow.getStartWindowTime()),
//                    TimestampUtil.generateTimestampMinusMinutesMillis(now, timeWindow.getEndWindowTime()),
//                    eventFields, errorCode);
//
//            log.debug("CountAggregatorImpl field: {} value: {}  countError: {}", resolve.getName(), resolve.getValue(), count);
//            return count + CURRENT_ONE;
//        } catch (Exception e) {
//            log.warn("CountAggregatorImpl error when countError e: ", e);
//            throw new RuleFunctionException(e);
//        }
//    }
//
//    @Override
//    @BasicMetric("countChargeback")
//    public Integer countChargeback(PaymentCheckedField paymentCheckedField, PaymentModel paymentModel, TimeWindow timeWindow, List<PaymentCheckedField> list) {
//        return getCount(paymentCheckedField, paymentModel, timeWindow, list, analyticsChargebackRepository::countOperationByFieldWithGroupBy, false);
//    }
//
//    @Override
//    @BasicMetric("countRefund")
//    public Integer countRefund(PaymentCheckedField paymentCheckedField, PaymentModel paymentModel, TimeWindow timeWindow, List<PaymentCheckedField> list) {
//        return getCount(paymentCheckedField, paymentModel, timeWindow, list, analyticsRefundRepository::countOperationByFieldWithGroupBy, false);
//    }
//
//    @NotNull
//    private Integer getCount(PaymentCheckedField checkedField, PaymentModel paymentModel, TimeWindow timeWindow, List<PaymentCheckedField> list,
//                             AggregateGroupingFunction<String, String, Long, Long, List<FieldModel>, Integer> aggregateFunction) {
//        return getCount(checkedField, paymentModel, timeWindow, list, aggregateFunction, true);
//    }
//
//    @NotNull
//    private Integer getCount(PaymentCheckedField checkedField, PaymentModel paymentModel, TimeWindow timeWindow, List<PaymentCheckedField> list,
//                             AggregateGroupingFunction<String, String, Long, Long, List<FieldModel>, Integer> aggregateFunction, boolean withCurrent) {
//        try {
//            Instant now = paymentModel.getTimestamp() != null ? Instant.ofEpochMilli(paymentModel.getTimestamp()) : Instant.now();
//            FieldModel resolve = dbPaymentFieldResolver.resolve(checkedField, paymentModel);
//            List<FieldModel> eventFields = dbPaymentFieldResolver.resolveListFields(paymentModel, list);
//
//            if (StringUtils.isEmpty(resolve.getValue())) {
//                return withCurrent ? CURRENT_ONE : 0;
//            }
//
//            Integer count = aggregateFunction.accept(resolve.getName(), resolve.getValue(),
//                    TimestampUtil.generateTimestampMinusMinutesMillis(now, timeWindow.getStartWindowTime()),
//                    TimestampUtil.generateTimestampMinusMinutesMillis(now, timeWindow.getEndWindowTime()), eventFields);
//
//            log.debug("CountAggregatorImpl field: {} value: {}  count: {}", resolve.getName(), resolve.getValue(), count);
//            return withCurrent ? count + CURRENT_ONE : count;
//        } catch (Exception e) {
//            log.warn("CountAggregatorImpl error when getCount e: ", e);
//            throw new RuleFunctionException(e);
//        }
//    }
}
