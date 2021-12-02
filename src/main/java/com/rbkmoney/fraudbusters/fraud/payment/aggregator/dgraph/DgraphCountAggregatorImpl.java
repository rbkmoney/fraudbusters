package com.rbkmoney.fraudbusters.fraud.payment.aggregator.dgraph;

import com.rbkmoney.fraudbusters.aspect.BasicMetric;
import com.rbkmoney.fraudbusters.constant.ChargebackStatus;
import com.rbkmoney.fraudbusters.constant.PaymentStatus;
import com.rbkmoney.fraudbusters.constant.RefundStatus;
import com.rbkmoney.fraudbusters.fraud.constant.DgraphEntity;
import com.rbkmoney.fraudbusters.fraud.constant.PaymentCheckedField;
import com.rbkmoney.fraudbusters.fraud.model.PaymentModel;
import com.rbkmoney.fraudbusters.fraud.payment.aggregator.dgraph.query.builder.DgraphAggregationQueryBuilderService;
import com.rbkmoney.fraudbusters.fraud.payment.resolver.DgraphEntityResolver;
import com.rbkmoney.fraudbusters.repository.DgraphAggregatesRepository;
import com.rbkmoney.fraudo.model.TimeWindow;
import com.rbkmoney.fraudo.payment.aggregator.CountPaymentAggregator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;
import java.util.List;

import static com.rbkmoney.fraudbusters.util.DgraphAggregatorUtils.createFiltersList;
import static com.rbkmoney.fraudbusters.util.DgraphAggregatorUtils.getTimestamp;

@Slf4j
@RequiredArgsConstructor
public class DgraphCountAggregatorImpl implements CountPaymentAggregator<PaymentModel, PaymentCheckedField> {

    private final DgraphAggregationQueryBuilderService dgraphCountQueryBuilderService;
    private final DgraphEntityResolver dgraphEntityResolver;
    private final DgraphAggregatesRepository dgraphAggregatesRepository;

    @Override
    @BasicMetric("count")
    public Integer count(PaymentCheckedField checkedField,
                         PaymentModel paymentModel,
                         TimeWindow timeWindow,
                         List<PaymentCheckedField> list) {
        return getCount(checkedField, paymentModel, timeWindow, list, DgraphEntity.PAYMENT, null);
    }

    @Override
    @BasicMetric("countSuccess")
    public Integer countSuccess(PaymentCheckedField checkedField,
                                PaymentModel paymentModel,
                                TimeWindow timeWindow,
                                List<PaymentCheckedField> list) {
        return getCount(
                checkedField, paymentModel, timeWindow, list, DgraphEntity.PAYMENT, PaymentStatus.captured.name()
        );
    }

    @Override
    @BasicMetric("countError")
    public Integer countError(PaymentCheckedField checkedField,
                              PaymentModel paymentModel,
                              TimeWindow timeWindow,
                              String errorCode,
                              List<PaymentCheckedField> list) {
        return getCount(
                checkedField, paymentModel, timeWindow, list, DgraphEntity.PAYMENT, PaymentStatus.failed.name()
        );
    }

    @Override
    @BasicMetric("countChargeback")
    public Integer countChargeback(
            PaymentCheckedField checkedField,
            PaymentModel paymentModel,
            TimeWindow timeWindow,
            List<PaymentCheckedField> list) {
        return getCount(
                checkedField, paymentModel, timeWindow, list, DgraphEntity.CHARGEBACK, ChargebackStatus.accepted.name()
        );
    }

    @Override
    @BasicMetric("countRefund")
    public Integer countRefund(
            PaymentCheckedField checkedField,
            PaymentModel paymentModel,
            TimeWindow timeWindow,
            List<PaymentCheckedField> list) {
        return getCount(
                checkedField, paymentModel, timeWindow, list, DgraphEntity.REFUND, RefundStatus.succeeded.name()
        );
    }

    private Integer getCount(PaymentCheckedField checkedField,
                             PaymentModel paymentModel,
                             TimeWindow timeWindow,
                             List<PaymentCheckedField> fields,
                             DgraphEntity targetEntity,
                             String status) {
        Instant timestamp = getTimestamp(paymentModel);
        Instant startWindowTime = timestamp.minusMillis(timeWindow.getStartWindowTime());
        Instant endWindowTime = timestamp.minusMillis(timeWindow.getEndWindowTime());
        List<PaymentCheckedField> filters = createFiltersList(checkedField, fields);

        String countQuery = dgraphCountQueryBuilderService.getQuery(
                dgraphEntityResolver.resolvePaymentCheckedField(checkedField),
                targetEntity,
                dgraphEntityResolver.resolvePaymentCheckedFieldsToMap(filters),
                paymentModel,
                startWindowTime,
                endWindowTime,
                status
        );
        return dgraphAggregatesRepository.getCount(countQuery);
    }

}
