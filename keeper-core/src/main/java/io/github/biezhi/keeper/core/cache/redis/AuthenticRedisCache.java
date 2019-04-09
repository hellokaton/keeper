package io.github.biezhi.keeper.core.cache.redis;

import io.github.biezhi.keeper.core.authc.AuthenticInfo;
import io.github.biezhi.keeper.core.authc.impl.SimpleAuthenticInfo;
import io.github.biezhi.keeper.utils.JsonUtil;
import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * @author biezhi
 * @date 2019-04-07
 */
public class AuthenticRedisCache extends RedisCache<AuthenticInfo> {

    public AuthenticRedisCache(StringRedisTemplate stringRedisTemplate) {
        this(stringRedisTemplate, "keeper:authentic:");
    }

    public AuthenticRedisCache(StringRedisTemplate stringRedisTemplate, String prefix) {
        super(stringRedisTemplate, prefix);
    }

    @Override
    public AuthenticInfo get(String key) {
        String json = stringRedisTemplate.opsForValue().get(prefix + key);
        return JsonUtil.toBean(json, SimpleAuthenticInfo.class);
    }

}
