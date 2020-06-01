package com.rbkmoney.fraudbusters.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum EventSource {

    ANALYTIC_EVENTS_SINK("analytic.events_sink"),
    FRAUD_EVENTS_UNIQUE("fraud.events_unique"),
    FRAUD_EVENTS_P_TO_P("fraud.events_p_to_p");

    @Getter
    private final String table;

}
