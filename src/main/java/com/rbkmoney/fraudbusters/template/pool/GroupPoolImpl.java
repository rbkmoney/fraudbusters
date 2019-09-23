package com.rbkmoney.fraudbusters.template.pool;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class GroupPoolImpl implements Pool<List<String>> {

    private Map<String, List<String>> references = new ConcurrentHashMap<>();

    @Override
    public void add(String key, List<String> reference) {
        references.put(key, reference);
    }

    @Override
    public List<String> get(String key) {
        return key != null ? references.get(key) : null;
    }

    @Override
    public void remove(String key) {
        references.remove(key);
    }
}
