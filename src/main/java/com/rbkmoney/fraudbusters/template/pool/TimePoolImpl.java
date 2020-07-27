package com.rbkmoney.fraudbusters.template.pool;

import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@ToString
@RequiredArgsConstructor
public class TimePoolImpl<T> implements TimePool<T> {

    private final Map<String, TreeMap<Long, T>> references = new ConcurrentHashMap<>();
    private final String poolName;

    @Override
    public void add(String key, Long timestamp, T value) {
        log.debug("TimePoolImpl add key: {} timestamp: {} value: {}", key, timestamp, value);
        TreeMap<Long, T> list = references.get(key);
        if (list == null) {
            list = new TreeMap<>();
        }
        list.put(timestamp, value);
        references.put(key, list);
    }

    @Override
    public T get(String key, Long timestamp) {
        T value = key != null && timestamp != null && references.containsKey(key) && !CollectionUtils.isEmpty(references.get(key)) ?
                references.get(key).lowerEntry(timestamp).getValue() : null;
        log.debug("TimePoolImpl get key: {} timestamp: {} value: {}", key, timestamp, value);
        return value;
    }

    @Override
    public void remove(String key, Long timestamp) {
        log.debug("TimePoolImpl remove key: {} timestamp: {}", key, timestamp);
        if (timestamp != null) {
            references.get(key).remove(timestamp);
        } else {
            references.remove(key);
        }
    }

    @Override
    public boolean contains(String key, Long timestamp) {
        return references.containsKey(key) && references.get(key) != null && references.get(key).containsKey(timestamp);
    }

    @Override
    public int size() {
        return references.size();
    }

    @Override
    public String getName() {
        return poolName;
    }

}
