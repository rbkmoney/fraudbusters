package com.rbkmoney.fraudbusters.pool;

import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@ToString
@RequiredArgsConstructor
public class HistoricalPoolImpl<T> implements HistoricalPool<T> {

    private final Map<String, TreeMap<Long, T>> references = new ConcurrentHashMap<>();
    private final String poolName;

    @Override
    public void add(String key, Long timestamp, T value) {
        log.debug("HistoricalPoolImpl add key: {} timestamp: {} value: {}", key, timestamp, value);
        TreeMap<Long, T> list = references.get(key);
        if (list == null) {
            list = new TreeMap<>();
        }
        list.put(timestamp, value);
        references.put(key, list);
    }

    @Override
    public T get(String key, Long timestamp) {
        T value = null;
        if (key != null && timestamp != null) {
            TreeMap<Long, T> treeMap = references.get(key);
            if (treeMap != null && !treeMap.isEmpty()) {
                Map.Entry<Long, T> greatestLeaf = treeMap.lowerEntry(timestamp);
                if (greatestLeaf != null) {
                    value = greatestLeaf.getValue();
                }
            }
        }
        log.debug("HistoricalPoolImpl get key: {} timestamp: {} value: {}", key, timestamp, value);
        return value;
    }

    @Override
    public void remove(String key, Long timestamp) {
        log.debug("HistoricalPoolImpl remove key: {} timestamp: {}", key, timestamp);
        if (timestamp != null) {
            references.get(key).remove(timestamp);
        } else {
            references.remove(key);
        }
    }

    @Override
    public void cleanUntil(String key, Long timestamp) {
        boolean until = references.get(key) != null
                    && references.get(key).lowerEntry(timestamp) != null
                    && references.get(key).lowerEntry(timestamp).getKey() < timestamp;
        if (until) {
            Long nearTimestamp = references.get(key).lowerEntry(timestamp).getKey();
            references.get(key).remove(nearTimestamp);
            cleanUntil(key, nearTimestamp);
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
    public int deepSize() {
        return references.values().stream()
                .map(TreeMap::size)
                .mapToInt(Integer::intValue)
                .sum();
    }

    @Override
    public Set<String> keySet() {
        return references.keySet();
    }

    @Override
    public String getName() {
        return poolName;
    }

}
