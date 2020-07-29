package com.rbkmoney.fraudbusters.pool;

public interface Pool<T> extends CheckedMetricPool {

    void add(String key, T parseContext);

    T get(String key);

    void remove(String key);

}
