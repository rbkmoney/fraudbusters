package com.rbkmoney.fraudbusters.template.pool;

public interface Pool<T> extends CheckedMetricPool {

    void add(String key, T parseContext);

    T get(String key);

    void remove(String key);

}
