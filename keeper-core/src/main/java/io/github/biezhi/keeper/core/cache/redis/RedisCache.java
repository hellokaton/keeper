package io.github.biezhi.keeper.core.cache.redis;

import io.github.biezhi.keeper.core.cache.Cache;
import io.github.biezhi.keeper.utils.JsonUtil;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @author biezhi
 * @date 2019-04-07
 */
public class RedisCache<V> implements Cache<String, V> {

    protected final StringRedisTemplate stringRedisTemplate;

    protected final String prefix;

    public RedisCache(StringRedisTemplate stringRedisTemplate) {
        this(stringRedisTemplate, "");
    }

    public RedisCache(StringRedisTemplate stringRedisTemplate, String prefix) {
        this.stringRedisTemplate = stringRedisTemplate;
        this.prefix = prefix;
    }

    @Override
    public void set(String key, V value) {
        if (value instanceof String) {
            stringRedisTemplate.opsForValue().set(prefix + key, value.toString());
        } else {
            String json = JsonUtil.toJSONString(value);
            stringRedisTemplate.opsForValue().set(prefix + key, json);
        }
    }

    @Override
    public void set(String key, V value, long seconds) {
        if (value instanceof String) {
            stringRedisTemplate.opsForValue().set(prefix + key, value.toString());
        } else {
            String json = JsonUtil.toJSONString(value);
            stringRedisTemplate.opsForValue().set(prefix + key, json);
        }
        if (seconds > 0) {
            stringRedisTemplate.expire(prefix + key, seconds, TimeUnit.SECONDS);
        }
    }

    @Override
    public <T> T get(String key, Class<T> type) {
        String json = stringRedisTemplate.opsForValue().get(key);
        if (String.class.equals(type)) {
            return (T) json;
        }
        if (Integer.class.equals(type)) {
            return (T) Integer.valueOf(json);
        }
        if (Long.class.equals(type)) {
            return (T) Long.valueOf(json);
        }
        return JsonUtil.toBean(json, type);
    }

    @Override
    public void delWith(String keyPrefix) {
        Set<String> keys = stringRedisTemplate.keys(keyPrefix + "*");
        stringRedisTemplate.delete(keys);
    }

    @Override
    public boolean exists(String key) {
        if (null == key) {
            return false;
        }
        Boolean hasKey = stringRedisTemplate.hasKey(prefix + key);
        return null == hasKey ? false : hasKey;
    }

    @Override
    public boolean expire(String key) {
        if (null == key) {
            return false;
        }
        Long expire = stringRedisTemplate.getExpire(prefix + key);
        return null == expire || expire < 2;
    }

    @Override
    public void remove(String key) {
        String delKey = prefix + key;
        stringRedisTemplate.delete(delKey);
        stringRedisTemplate.hasKey(delKey);
    }

    @Override
    public Set<String> keySet() {
        return stringRedisTemplate.keys(prefix);
    }

    @Override
    public void clear() {
        stringRedisTemplate.delete(keySet());
    }

}
