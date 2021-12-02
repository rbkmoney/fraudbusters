package com.rbkmoney.fraudbusters.fraud.payment.aggregator.dgraph.query.builder;

import com.rbkmoney.fraudbusters.fraud.constant.DgraphEntity;
import com.rbkmoney.fraudbusters.fraud.constant.PaymentCheckedField;
import com.rbkmoney.fraudbusters.fraud.model.PaymentModel;

import java.time.Instant;
import java.util.Map;
import java.util.Set;

public interface DgraphAggregationQueryBuilderService {

    String getQuery(DgraphEntity rootEntity,
                    DgraphEntity targetEntity,
                    Map<DgraphEntity, Set<PaymentCheckedField>> dgraphEntityMap,
                    PaymentModel paymentModel,
                    Instant startWindowTime,
                    Instant endWindowTime,
                    String status);

}
