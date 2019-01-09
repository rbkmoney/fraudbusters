package com.rbkmoney.fraudbusters.fraud.aggragator;

@FunctionalInterface
public interface AggregateFunction<T, U, V, W, R> {

    R accept(T t, U u, V v, W w);

}
