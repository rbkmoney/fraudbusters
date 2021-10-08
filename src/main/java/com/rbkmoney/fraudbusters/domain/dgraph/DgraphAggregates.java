package com.rbkmoney.fraudbusters.domain.dgraph;

import lombok.Data;

@Data
public class DgraphAggregates {

    private int count;
    private long sum;
    private DgraphMetrics metrics;

    public DgraphAggregates setQueryMetrics(DgraphMetrics metrics) {
        this.metrics = metrics;
        return this;
    }

}
