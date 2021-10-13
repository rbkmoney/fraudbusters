package com.rbkmoney.fraudbusters.domain.dgraph;

import lombok.Data;

@Data
public class DgraphMetrics {

    private long totalMs;
    private long assignTimestampMs;
    private long encodingMs;
    private long parsingMs;
    private long processingMs;

}
