package com.rbkmoney.fraudbusters.aspect;

import io.micrometer.core.annotation.Incubating;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.Tags;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.lang.NonNullApi;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.reflect.Method;
import java.util.function.Function;

@Aspect
@NonNullApi
@Incubating(since = "1.0.0")
public class SimpleMeasureAspect {


    public static final String DEFAULT_METRIC_NAME = "method.timed";

    /**
     * Tag key for an exception.
     *
     * @since 1.1.0
     */
    public static final String EXCEPTION_TAG = "exception";
    public static final String COUNT = ".count";
    public static final String TIMER = ".timer";

    private final MeterRegistry registry;
    private final Function<ProceedingJoinPoint, Iterable<Tag>> tagsBasedOnJoinPoint;

    public SimpleMeasureAspect(MeterRegistry registry) {
        this(registry, pjp ->
                Tags.of("class", pjp.getStaticPart().getSignature().getDeclaringTypeName(),
                        "method", pjp.getStaticPart().getSignature().getName())
        );
    }

    public SimpleMeasureAspect(MeterRegistry registry, Function<ProceedingJoinPoint, Iterable<Tag>> tagsBasedOnJoinPoint) {
        this.registry = registry;
        this.tagsBasedOnJoinPoint = tagsBasedOnJoinPoint;
    }

    @Around("execution (@com.rbkmoney.fraudbusters.aspect.BasicMetric * *.*(..))")
    public Object timedMethod(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        Method method = ((MethodSignature) proceedingJoinPoint.getSignature()).getMethod();
        BasicMetric basicMetric = method.getAnnotation(BasicMetric.class);
        final String metricName = basicMetric.value().isEmpty() ? DEFAULT_METRIC_NAME : basicMetric.value();
        Timer.Sample sample = Timer.start(registry);

        try {
            return proceedingJoinPoint.proceed();
        } finally {
            try {
                registry.counter(metricName + COUNT, basicMetric.extraTags())
                        .increment();
                sample.stop(registry.timer(metricName + TIMER, basicMetric.extraTags()));
            } catch (Exception e) {
                // ignoring on purpose
            }
        }
    }

}