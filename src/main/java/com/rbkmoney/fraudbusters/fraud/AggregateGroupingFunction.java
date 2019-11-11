package com.rbkmoney.fraudbusters.fraud;

@FunctionalInterface
public interface AggregateGroupingFunction<T, U, V, W, P, R> {

    R accept(T t, U u, V v, W w, P p);

}
