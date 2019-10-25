package com.rbkmoney.fraudbusters.config;

import com.rbkmoney.fraudbusters.aspect.SimpleMeasureAspect;
import io.micrometer.statsd.StatsdMeterRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@Configuration
@EnableAspectJAutoProxy
public class MetricsConfiguration {

    @Bean
    SimpleMeasureAspect simpleMeasureAspect(StatsdMeterRegistry registry) {
        return new SimpleMeasureAspect(registry);
    }

}