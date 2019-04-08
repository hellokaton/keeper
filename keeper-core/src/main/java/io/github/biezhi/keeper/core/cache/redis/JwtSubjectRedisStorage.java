package io.github.biezhi.keeper.core.cache.redis;

import io.github.biezhi.keeper.core.subject.JwtSubject;
import io.github.biezhi.keeper.core.subject.Subject;
import io.github.biezhi.keeper.utils.JsonUtil;
import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * @author biezhi
 * @date 2019-04-07
 */
public class JwtSubjectRedisStorage extends RedisCache<Subject> {

    public JwtSubjectRedisStorage(StringRedisTemplate stringRedisTemplate) {
        this(stringRedisTemplate, "keeper:subject:");
    }

    public JwtSubjectRedisStorage(StringRedisTemplate stringRedisTemplate, String prefix) {
        super(stringRedisTemplate, prefix);
    }

    @Override
    public Subject get(String key) {
        String json = stringRedisTemplate.opsForValue().get(prefix + key);
        return JsonUtil.toBean(json, JwtSubject.class);
    }

}
