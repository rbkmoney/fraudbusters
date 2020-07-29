package com.rbkmoney.fraudbusters.pool;

import java.util.Set;

public interface HistoricalPool<T> extends CheckedMetricPool {

    void add(String key, Long timestamp, T parseContext);

    T get(String key, Long timestamp);

    void remove(String key, Long timestamp);

    void cleanUntil(String key, Long timestamp);

    boolean contains(String key, Long timestamp);

    int size();

    int deepSize();

    Set<String> keySet();

}
