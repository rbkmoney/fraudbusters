package com.rbkmoney.fraudbusters.fraud.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum DgraphTargetAggregationType {

    PAYMENT("payments"),
    REFUND("refunds"),
    CHARGEBACK("chargebacks");

    private final String fieldName;

}
