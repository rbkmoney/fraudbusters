package com.rbkmoney.fraudbusters.fraud.aggragator;

import com.rbkmoney.fraudbusters.aspect.BasicMetric;
import com.rbkmoney.fraudbusters.exception.RuleFunctionException;
import com.rbkmoney.fraudbusters.fraud.resolver.DBPaymentFieldResolver;
import com.rbkmoney.fraudbusters.repository.EventRepository;
import com.rbkmoney.fraudbusters.util.TimestampUtil;
import com.rbkmoney.fraudo.aggregator.UniqueValueAggregator;
import com.rbkmoney.fraudo.constant.PaymentCheckedField;
import com.rbkmoney.fraudo.model.PaymentModel;
import com.rbkmoney.fraudo.model.TimeWindow;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class UniqueValueAggregatorImpl implements UniqueValueAggregator<PaymentModel, PaymentCheckedField> {

    private static final int CURRENT_ONE = 1;
    private final EventRepository eventRepository;
    private final DBPaymentFieldResolver dbPaymentFieldResolver;

    @Override
    @BasicMetric("countUniqueValueWindowed")
    public Integer countUniqueValue(PaymentCheckedField countField,
                                    PaymentModel fraudModel,
                                    PaymentCheckedField onField,
                                    TimeWindow timeWindow,
                                    List<PaymentCheckedField> list) {
        try {
            Instant now = Instant.now();
            DBPaymentFieldResolver.FieldModel resolve = dbPaymentFieldResolver.resolve(countField, fraudModel);
            List<DBPaymentFieldResolver.FieldModel> fieldModels = dbPaymentFieldResolver.resolveListFields(fraudModel, list);
            Integer uniqCountOperation = eventRepository.uniqCountOperationWithGroupBy(resolve.getName(), resolve.getValue(),
                    dbPaymentFieldResolver.resolve(onField),
                    TimestampUtil.generateTimestampMinusMinutes(now, timeWindow.getStartWindowTime()),
                    TimestampUtil.generateTimestampMinusMinutes(now, timeWindow.getEndWindowTime()),
                    fieldModels);
            return uniqCountOperation + CURRENT_ONE;
        } catch (Exception e) {
            log.warn("UniqueValueAggregatorImpl error when getCount e: ", e);
            throw new RuleFunctionException(e);
        }
    }
}
