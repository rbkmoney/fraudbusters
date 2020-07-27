package com.rbkmoney.fraudbusters.template.pool;

public interface TimePool<T>  extends CheckedMetricPool {

    void add(String key, Long timestamp, T parseContext);

    T get(String key, Long timestamp);

    void remove(String key, Long timestamp);

    boolean contains(String key, Long timestamp);

    int size();

}
