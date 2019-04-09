package io.github.biezhi.keeper.core.cache.redis;

import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * LogoutRedisCache
 *
 * @author biezhi
 * @date 2019-04-09
 */
public class LogoutRedisCache extends RedisCache<String> {

    public LogoutRedisCache(StringRedisTemplate stringRedisTemplate) {
        this(stringRedisTemplate, "");
    }

    public LogoutRedisCache(StringRedisTemplate stringRedisTemplate, String prefix) {
        super(stringRedisTemplate, prefix);
    }

    @Override
    public String get(String key) {
        return stringRedisTemplate.opsForValue().get(prefix + key);
    }

}
