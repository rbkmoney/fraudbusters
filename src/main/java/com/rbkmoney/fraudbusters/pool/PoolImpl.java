package com.rbkmoney.fraudbusters.pool;

import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@ToString
@RequiredArgsConstructor
public class PoolImpl<T> implements Pool<T> {

    private final Map<String, T> map = new ConcurrentHashMap<>();

    private final String poolName;

    @Override
    public void add(String key, T reference) {
        map.put(key, reference);
    }

    @Override
    public T get(String key) {
        return key != null ? map.get(key) : null;
    }

    @Override
    public void remove(String key) {
        map.remove(key);
    }

    @Override
    public int size() {
        return map.size();
    }

    @Override
    public String getName() {
        return poolName;
    }
}
