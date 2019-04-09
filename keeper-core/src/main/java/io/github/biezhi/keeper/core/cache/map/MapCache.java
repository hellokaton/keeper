package io.github.biezhi.keeper.core.cache.map;

import io.github.biezhi.keeper.core.cache.Cache;

import java.time.Duration;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author biezhi
 * @date 2019-04-07
 */
public class MapCache<K, V> implements Cache<K, V> {

    private Map<K, V> cache;

    public MapCache() {
        this.cache = new ConcurrentHashMap<>();
    }

    public MapCache(int capacity) {
        this.cache = new ConcurrentHashMap<>(capacity);
    }

    public MapCache(Map<K, V> cache) {
        this.cache = cache;
    }

    @Override
    public void set(K key, V value) {
        cache.putIfAbsent(key, value);
    }

    @Deprecated
    @Override
    public void set(K key, V value, Duration expiresTime) {
        cache.putIfAbsent(key, value);
    }

    @Override
    public V get(K key) {
        return cache.get(key);
    }

    @Override
    public boolean exists(K key) {
        return cache.containsKey(key);
    }

    @Override
    public boolean expire(K key) {
        return !cache.containsKey(key);
    }

    @Override
    public void remove(K key) {
        cache.remove(key);
    }

    @Override
    public Set<K> keySet() {
        return cache.keySet();
    }

    @Override
    public void clear() {
        cache.clear();
    }

}
