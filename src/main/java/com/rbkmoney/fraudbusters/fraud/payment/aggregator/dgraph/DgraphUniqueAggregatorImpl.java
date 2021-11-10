package com.rbkmoney.fraudbusters.fraud.payment.aggregator.dgraph;

import com.rbkmoney.fraudbusters.fraud.constant.PaymentCheckedField;
import com.rbkmoney.fraudbusters.fraud.model.PaymentModel;
import com.rbkmoney.fraudo.aggregator.UniqueValueAggregator;
import com.rbkmoney.fraudo.model.TimeWindow;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class DgraphUniqueAggregatorImpl implements UniqueValueAggregator<PaymentModel, PaymentCheckedField> {

    // нужно посчитать количество уникальных почт у токена
    @Override
    public Integer countUniqueValue(PaymentCheckedField countField, // токен
                                    PaymentModel payoutModel,
                                    PaymentCheckedField onField, // почта
                                    TimeWindow timeWindow,
                                    List<PaymentCheckedField> fields // Условия
    ) {
        return null;
    }

}
