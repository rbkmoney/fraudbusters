package com.rbkmoney.fraudbusters.template.pool.time;

public interface TimePool<T> {

    void add(String key, Long timestamp, T parseContext);

    T get(String key, Long timestamp);

    void remove(String key, Long timestamp);

    int size();

}
