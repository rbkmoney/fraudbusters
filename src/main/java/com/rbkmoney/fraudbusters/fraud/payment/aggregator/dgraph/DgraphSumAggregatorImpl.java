package com.rbkmoney.fraudbusters.fraud.payment.aggregator.dgraph;

import com.rbkmoney.fraudbusters.aspect.BasicMetric;
import com.rbkmoney.fraudbusters.constant.ChargebackStatus;
import com.rbkmoney.fraudbusters.constant.PaymentStatus;
import com.rbkmoney.fraudbusters.constant.RefundStatus;
import com.rbkmoney.fraudbusters.fraud.constant.DgraphEntity;
import com.rbkmoney.fraudbusters.fraud.constant.PaymentCheckedField;
import com.rbkmoney.fraudbusters.fraud.model.PaymentModel;
import com.rbkmoney.fraudbusters.fraud.payment.resolver.DgraphEntityResolver;
import com.rbkmoney.fraudbusters.repository.DgraphAggregatesRepository;
import com.rbkmoney.fraudo.model.TimeWindow;
import com.rbkmoney.fraudo.payment.aggregator.SumPaymentAggregator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;
import java.util.List;

import static com.rbkmoney.fraudbusters.util.DgraphAggregatorUtils.createFiltersList;
import static com.rbkmoney.fraudbusters.util.DgraphAggregatorUtils.getTimestamp;

@Slf4j
@RequiredArgsConstructor
public class DgraphSumAggregatorImpl implements SumPaymentAggregator<PaymentModel, PaymentCheckedField> {

    private final DgraphAggregationQueryBuilderService dgraphAggregationQueryBuilderService;
    private final DgraphEntityResolver dgraphEntityResolver;
    private final DgraphAggregatesRepository dgraphAggregatesRepository;

    @Override
    @BasicMetric("sum")
    public Double sum(PaymentCheckedField checkedField,
                      PaymentModel model,
                      TimeWindow timeWindow,
                      List<PaymentCheckedField> fields) {
        return getSum(checkedField, model, timeWindow, fields, DgraphEntity.PAYMENT, null);
    }

    @Override
    @BasicMetric("sumSuccess")
    public Double sumSuccess(PaymentCheckedField checkedField,
                             PaymentModel model,
                             TimeWindow timeWindow,
                             List<PaymentCheckedField> fields) {
        return getSum(checkedField, model, timeWindow, fields, DgraphEntity.PAYMENT, PaymentStatus.captured.name());
    }

    @Override
    @BasicMetric("sumError")
    public Double sumError(PaymentCheckedField checkedField,
                           PaymentModel model,
                           TimeWindow timeWindow,
                           String errorCode,
                           List<PaymentCheckedField> fields) {
        return getSum(checkedField, model, timeWindow, fields, DgraphEntity.PAYMENT, PaymentStatus.failed.name());
    }

    @Override
    public Double sumChargeback(PaymentCheckedField checkedField,
                                PaymentModel model,
                                TimeWindow timeWindow,
                                List<PaymentCheckedField> fields) {
        return getSum(
                checkedField, model, timeWindow, fields, DgraphEntity.CHARGEBACK, ChargebackStatus.accepted.name()
        );
    }

    @Override
    public Double sumRefund(PaymentCheckedField checkedField,
                            PaymentModel model,
                            TimeWindow timeWindow,
                            List<PaymentCheckedField> fields) {
        return getSum(checkedField, model, timeWindow, fields, DgraphEntity.REFUND, RefundStatus.succeeded.name());
    }

    private Double getSum(PaymentCheckedField checkedField,
                             PaymentModel paymentModel,
                             TimeWindow timeWindow,
                             List<PaymentCheckedField> fields,
                             DgraphEntity targetEntity,
                             String status) {
        Instant timestamp = getTimestamp(paymentModel);
        Instant startWindowTime = timestamp.minusMillis(timeWindow.getStartWindowTime());
        Instant endWindowTime = timestamp.minusMillis(timeWindow.getEndWindowTime());
        List<PaymentCheckedField> filters = createFiltersList(checkedField, fields);

        String countQuery = dgraphAggregationQueryBuilderService.getSumQuery(
                dgraphEntityResolver.resolvePaymentCheckedField(checkedField),
                targetEntity,
                dgraphEntityResolver.resolvePaymentCheckedFieldsToMap(filters),
                paymentModel,
                startWindowTime,
                endWindowTime,
                status
        );
        return dgraphAggregatesRepository.getSum(countQuery);
    }

}
