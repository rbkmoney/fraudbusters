package com.rbkmoney.fraudbusters.domain.dgraph;

import lombok.Data;

import java.util.List;

@Data
public class DgraphTokenResponse {

    private List<DgraphToken> tokens;
    private List<DgraphAggregates> aggregates;
    private DgraphMetrics metrics;

}
