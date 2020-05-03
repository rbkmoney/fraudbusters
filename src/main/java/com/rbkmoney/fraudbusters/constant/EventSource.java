package com.rbkmoney.fraudbusters.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum EventSource {

    ANALYTIC_EVENTS_SINK("analytic.events_sink"),
    FRAUD_EVENTS_UNIQUE("fraud.events_unique");

    @Getter
    private final String table;

}
