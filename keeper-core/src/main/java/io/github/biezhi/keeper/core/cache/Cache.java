package io.github.biezhi.keeper.core.cache;

import java.time.Duration;
import java.util.Set;

/**
 * @author biezhi
 * @date 2019-04-07
 */
public interface Cache<K, V> {

    void put(K key, V value);

    void put(K key, V value, Duration expiresTime);

    V get(K key);

    boolean exists(K key);

    boolean expire(K key);

    void remove(K key);

    Set<K> keySet();

    void clear();

}
