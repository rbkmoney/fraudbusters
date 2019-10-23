package com.rbkmoney.fraudbusters.service;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.testcontainers.utility.ThrowingFunction;

@Component
@RequiredArgsConstructor
public class MetricService {

    private final MeterRegistry registry;

    public <T, R> R invokeWithMetrics(boolean isEnableMetric, String methodName, ThrowingFunction<T, R> function, T request) throws Exception {
        Timer.Sample sample = null;
        Counter counter = null;
        if (isEnableMetric) {
            sample = Timer.start(registry);
            counter = Counter
                    .builder(methodName)
                    .register(registry);
        }

        R response = function.apply(request);

        if (isEnableMetric) {
            counter.increment();
            sample.stop(registry.timer(methodName));
        }
        return response;
    }

}
