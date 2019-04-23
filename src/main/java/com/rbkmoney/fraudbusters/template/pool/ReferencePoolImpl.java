package com.rbkmoney.fraudbusters.template.pool;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ReferencePoolImpl implements Pool<String> {

    private Map<String, String> references = new ConcurrentHashMap<>();

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
