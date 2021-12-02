package com.rbkmoney.fraudbusters.fraud.payment.aggregator.dgraph;

import com.rbkmoney.fraudbusters.constant.PaymentStatus;
import com.rbkmoney.fraudbusters.fraud.constant.PaymentCheckedField;
import com.rbkmoney.fraudbusters.fraud.model.PaymentModel;
import com.rbkmoney.fraudbusters.fraud.payment.resolver.DgraphEntityResolver;
import com.rbkmoney.fraudbusters.repository.DgraphAggregatesRepository;
import com.rbkmoney.fraudo.aggregator.UniqueValueAggregator;
import com.rbkmoney.fraudo.model.TimeWindow;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;
import java.util.List;

import static com.rbkmoney.fraudbusters.util.DgraphAggregatorUtils.createFiltersList;
import static com.rbkmoney.fraudbusters.util.DgraphAggregatorUtils.getTimestamp;

@Slf4j
@RequiredArgsConstructor
public class DgraphUniqueAggregatorImpl implements UniqueValueAggregator<PaymentModel, PaymentCheckedField> {

    private final DgraphAggregationQueryBuilderService dgraphAggregationQueryBuilderService;
    private final DgraphEntityResolver dgraphEntityResolver;
    private final DgraphAggregatesRepository dgraphAggregatesRepository;

    @Override
    public Integer countUniqueValue(PaymentCheckedField countField,
                                    PaymentModel paymentModel,
                                    PaymentCheckedField onField,
                                    TimeWindow timeWindow,
                                    List<PaymentCheckedField> fields) {
        if (onField == PaymentCheckedField.MOBILE || onField == PaymentCheckedField.RECURRENT) {
            return 0; //TODO: реализовать подсчет
        }

        Instant timestamp = getTimestamp(paymentModel);
        Instant startWindowTime = timestamp.minusMillis(timeWindow.getStartWindowTime());
        Instant endWindowTime = timestamp.minusMillis(timeWindow.getEndWindowTime());
        List<PaymentCheckedField> filters = createFiltersList(countField, fields);

        String countQuery = dgraphAggregationQueryBuilderService.getUniqueQuery(
                dgraphEntityResolver.resolvePaymentCheckedField(countField),
                dgraphEntityResolver.resolvePaymentCheckedField(onField),
                dgraphEntityResolver.resolvePaymentCheckedFieldsToMap(filters),
                paymentModel,
                startWindowTime,
                endWindowTime,
                PaymentStatus.captured.name()
        );
        return dgraphAggregatesRepository.getCount(countQuery);
    }

}
