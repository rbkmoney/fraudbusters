package com.rbkmoney.fraudbusters.service;

import com.rbkmoney.fraudbusters.template.pool.CheckedMetricPool;
import com.rbkmoney.fraudbusters.template.pool.Pool;
import com.rbkmoney.fraudbusters.template.pool.TimePool;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class PoolMonitoringService {

    public static final String POOL_METRIC = "pool-metric-";
    private final List<TimePool> timePools;
    private final List<Pool> pools;
    private final MeterRegistry registry;

    @Value("${time.pool.cleanup.gap}")
    private Long timeGap;

    @Scheduled(fixedDelay = 3600000)
    public void cleanOldValues() {
        long now = Instant.now().toEpochMilli();
        if (!CollectionUtils.isEmpty(timePools)) {
            for (TimePool timePool : timePools) {
                Set<String> set = timePool.keySet();
                for (String key : set) {
                    long stampOfOldestData = now - Duration.ofDays(timeGap).toMillis();
                    Object o = timePool.get(key, stampOfOldestData);
                    if (o == null) {
                        timePool.cleanUntil(key, stampOfOldestData);
                    }
                }
            }
        }
    }

    public void addPoolsToMonitoring() {
        checkTimePool(timePools);
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

    private void checkTimePool(List<TimePool> pools) {
        log.trace("PoolMonitoringService checkTimePool: size: {} pools: {} ", pools.size(), pools);
        if (!CollectionUtils.isEmpty(pools)) {
            for (TimePool timePool : pools) {
                Gauge gauge = Gauge
                        .builder(POOL_METRIC + timePool.getName(), timePool, TimePool::deepSize)
                        .register(registry);
                log.trace("PoolMonitoringService checkTimePool gauge: {}", gauge.value());
            }
        }
    }

}
