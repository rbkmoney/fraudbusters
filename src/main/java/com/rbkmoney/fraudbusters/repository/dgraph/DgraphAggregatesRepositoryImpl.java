package com.rbkmoney.fraudbusters.repository.dgraph;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rbkmoney.fraudbusters.domain.dgraph.DgraphAggregates;
import com.rbkmoney.fraudbusters.domain.dgraph.DgraphMetrics;
import com.rbkmoney.fraudbusters.repository.DgraphAggregatesRepository;
import io.dgraph.DgraphClient;
import io.dgraph.DgraphProto;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@Lazy
public class DgraphAggregatesRepositoryImpl extends AbstractDgraphDao implements DgraphAggregatesRepository {

    private final ObjectMapper dgraphMapper;

    public DgraphAggregatesRepositoryImpl(DgraphClient dgraphClient,
                                          RetryTemplate dgraphRetryTemplate,
                                          ObjectMapper dgraphMapper) {
        super(dgraphClient, dgraphRetryTemplate);
        this.dgraphMapper = dgraphMapper;
    }

    @Override
    public Integer getCount(String query) {
        return getAggregates(query).getCount();
    }

    @Override
    public Long getSum(String query) {
        return getAggregates(query).getSum();
    }

    protected DgraphAggregates getAggregates(String query) {
        DgraphProto.Response response = processDgraphQuery(query);
        String responseJson = response.getJson().toStringUtf8();
        log.debug("Received json with aggregates (query: {}): {}", query, responseJson);
        DgraphAggregatesDecorator dgraphAggregates = convertToObject(responseJson, DgraphAggregatesDecorator.class);
        log.debug("Received TokenResponse for query {}: {}", query, dgraphAggregates);
        DgraphAggregates aggregates = dgraphAggregates.aggregates.stream()
                .findFirst()
                .orElse(new DgraphAggregates())
                .setQueryMetrics(toMetrics(response.getLatency()));
        return aggregates;
    }

    protected <T> T convertToObject(String json, Class<T> clazz) {
        try {
            return dgraphMapper.readValue(json, clazz);
        } catch (JsonProcessingException ex) {
            throw new RuntimeException("exc", ex);
        }
    }

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

    @Data
    private static class DgraphAggregatesDecorator {

        private List<DgraphAggregates> aggregates;

    }

}
