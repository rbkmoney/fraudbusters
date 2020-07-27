package com.rbkmoney.fraudbusters.service;

import com.rbkmoney.fraudbusters.template.pool.CheckedMetricPool;
import com.rbkmoney.fraudbusters.template.pool.Pool;
import com.rbkmoney.fraudbusters.template.pool.TimePool;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PoolMonitoringService {

    public static final String POOL_METRIC = "pool-metric-";
    private final List<TimePool> timePools;
    private final List<Pool> pools;
    private final MeterRegistry registry;

    public void addPoolsToMonitoring() {
        checkPool(timePools);
        checkPool(pools);
    }

    private void checkPool(List<? extends CheckedMetricPool> pools) {
        log.trace("PoolMonitoringService pools: {} pools: {}", pools.size(), pools);
        if (!CollectionUtils.isEmpty(pools)) {
            for (CheckedMetricPool timePool : pools) {
                Gauge gauge = Gauge
                        .builder(POOL_METRIC + timePool.getName(), timePool, CheckedMetricPool::size)
                        .register(registry);
                log.trace("PoolMonitoringService gauge: {}", gauge.value());
            }
        }
    }

}
