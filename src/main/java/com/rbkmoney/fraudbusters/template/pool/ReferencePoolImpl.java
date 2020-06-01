package com.rbkmoney.fraudbusters.template.pool;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ReferencePoolImpl implements Pool<String> {

    private final Map<String, String> references = new ConcurrentHashMap<>();

    @Override
    public void add(String key, String reference) {
        references.put(key, reference);
    }

    @Override
    public String get(String key) {
        return key != null ? references.get(key) : null;
    }

    @Override
    public void remove(String key) {
        references.remove(key);
    }
}
