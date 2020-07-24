package com.rbkmoney.fraudbusters.template.pool;

import lombok.ToString;
import org.springframework.util.CollectionUtils;

import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

@ToString
public class TimePoolImpl<T> implements TimePool<T> {

    private final Map<String, TreeMap<Long, T>> references = new ConcurrentHashMap<>();

    @Override
    public void add(String key, Long timestamp, T reference) {
        TreeMap<Long, T> list = references.get(key);
        if (list == null) {
            list = new TreeMap<>();
        }
        list.put(timestamp, reference);
        references.put(key, list);
    }

    @Override
    public T get(String key, Long timestamp) {
        return key != null && timestamp != null && references.containsKey(key) && !CollectionUtils.isEmpty(references.get(key)) ?
                references.get(key).lowerEntry(timestamp).getValue() : null;
    }

    @Override
    public void remove(String key, Long timestamp) {
        if (timestamp != null) {
            references.get(key).remove(timestamp);
        } else {
            references.remove(key);
        }
    }

    @Override
    public int size() {
        return references.size();
    }
}
