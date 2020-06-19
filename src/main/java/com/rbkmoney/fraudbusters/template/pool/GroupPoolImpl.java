package com.rbkmoney.fraudbusters.template.pool;

import lombok.ToString;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@ToString
public class GroupPoolImpl implements Pool<List<String>> {

    private final Map<String, List<String>> references = new ConcurrentHashMap<>();

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

    @Override
    public int size() {
        return references.size();
    }
}
