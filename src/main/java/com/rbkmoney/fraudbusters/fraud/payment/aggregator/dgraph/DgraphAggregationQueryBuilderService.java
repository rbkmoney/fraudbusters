package com.rbkmoney.fraudbusters.fraud.payment.aggregator.dgraph;

import com.rbkmoney.fraudbusters.fraud.constant.DgraphEntity;
import com.rbkmoney.fraudbusters.fraud.constant.PaymentCheckedField;
import com.rbkmoney.fraudbusters.fraud.model.PaymentModel;

import java.time.Instant;
import java.util.Map;
import java.util.Set;

public interface DgraphAggregationQueryBuilderService {

    String getCountQuery(DgraphEntity rootEntity,
                         DgraphEntity targetEntity,
                         Map<DgraphEntity, Set<PaymentCheckedField>> dgraphEntityMap,
                         PaymentModel paymentModel,
                         Instant startWindowTime,
                         Instant endWindowTime,
                         String status);

    String getSumQuery(DgraphEntity rootEntity,
                       DgraphEntity targetEntity,
                       Map<DgraphEntity, Set<PaymentCheckedField>> dgraphEntityMap,
                       PaymentModel paymentModel,
                       Instant startWindowTime,
                       Instant endWindowTime,
                       String status);

    String getUniqueQuery(DgraphEntity rootEntity,
                          DgraphEntity onField,
                          Map<DgraphEntity, Set<PaymentCheckedField>> dgraphEntityMap,
                          PaymentModel paymentModel,
                          Instant startWindowTime,
                          Instant endWindowTime,
                          String status);

}
