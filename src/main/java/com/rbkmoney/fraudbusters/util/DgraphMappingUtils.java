package com.rbkmoney.fraudbusters.util;

import com.rbkmoney.fraudbusters.domain.dgraph.DgraphMetrics;
import io.dgraph.DgraphProto;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DgraphMappingUtils {

    public static DgraphMetrics toMetrics(DgraphProto.Latency latency) {
        DgraphMetrics metrics = new DgraphMetrics();
        metrics.setTotalMs(toMs(latency.getTotalNs()));
        metrics.setAssignTimestampMs(toMs(latency.getAssignTimestampNs()));
        metrics.setEncodingMs(toMs(latency.getEncodingNs()));
        metrics.setParsingMs(toMs(latency.getParsingNs()));
        metrics.setProcessingMs(toMs(latency.getProcessingNs()));
        return metrics;
    }

    private static long toMs(long nanosec) {
        return nanosec / 1_000_000L;
    }

}
