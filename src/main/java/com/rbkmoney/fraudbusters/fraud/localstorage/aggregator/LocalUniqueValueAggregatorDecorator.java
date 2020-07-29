package com.rbkmoney.fraudbusters.fraud.localstorage.aggregator;

import com.rbkmoney.fraudbusters.aspect.BasicMetric;
import com.rbkmoney.fraudbusters.exception.RuleFunctionException;
import com.rbkmoney.fraudbusters.fraud.constant.PaymentCheckedField;
import com.rbkmoney.fraudbusters.fraud.localstorage.LocalResultStorageRepository;
import com.rbkmoney.fraudbusters.fraud.model.FieldModel;
import com.rbkmoney.fraudbusters.fraud.model.PaymentModel;
import com.rbkmoney.fraudbusters.fraud.payment.aggregator.UniqueValueAggregatorImpl;
import com.rbkmoney.fraudbusters.fraud.payment.resolver.DBPaymentFieldResolver;
import com.rbkmoney.fraudbusters.util.TimestampUtil;
import com.rbkmoney.fraudo.aggregator.UniqueValueAggregator;
import com.rbkmoney.fraudo.model.TimeWindow;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class LocalUniqueValueAggregatorDecorator implements UniqueValueAggregator<PaymentModel, PaymentCheckedField> {

    private final UniqueValueAggregatorImpl uniqueValueAggregator;
    private final DBPaymentFieldResolver dbPaymentFieldResolver;
    private final LocalResultStorageRepository localStorageRepository;

    @Override
    @BasicMetric("countUniqueValueWindowed")
    public Integer countUniqueValue(PaymentCheckedField countField,
                                    PaymentModel paymentModel,
                                    PaymentCheckedField onField,
                                    TimeWindow timeWindow,
                                    List<PaymentCheckedField> list) {
        try {
            Integer uniq = uniqueValueAggregator.countUniqueValue(countField, paymentModel, onField, timeWindow, list);
            Instant now = TimestampUtil.instantFromPaymentModel(paymentModel);
            FieldModel resolve = dbPaymentFieldResolver.resolve(countField, paymentModel);
            List<FieldModel> fieldModels = dbPaymentFieldResolver.resolveListFields(paymentModel, list);
            Integer localUniqCountOperation = localStorageRepository.uniqCountOperationWithGroupBy(resolve.getName(), resolve.getValue(),
                    dbPaymentFieldResolver.resolve(onField),
                    TimestampUtil.generateTimestampMinusMinutesMillis(now, timeWindow.getStartWindowTime()),
                    TimestampUtil.generateTimestampMinusMinutesMillis(now, timeWindow.getEndWindowTime()), fieldModels);
            int result = localUniqCountOperation + uniq;
            log.debug("LocalUniqueValueAggregatorDecorator countUniqueValue: {}", result);
            return result;
        } catch (Exception e) {
            log.warn("LocalUniqueValueAggregatorDecorator error when getCount e: ", e);
            throw new RuleFunctionException(e);
        }
    }
}
