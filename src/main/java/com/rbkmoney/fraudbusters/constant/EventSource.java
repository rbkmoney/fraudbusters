package com.rbkmoney.fraudbusters.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum EventSource {

    ANALYTIC_EVENTS_SINK("analytic.events_sink"),
    ANALYTIC_EVENTS_SINK_REFUND("analytic.events_sink_refund"),
    ANALYTIC_EVENTS_SINK_CHARGEBACK("analytic.events_sink_chargeback"),
    FRAUD_EVENTS_UNIQUE("fraud.events_unique"),
    FRAUD_EVENTS_P_TO_P("fraud.events_p_to_p");

    @Getter
    private final String table;

}
