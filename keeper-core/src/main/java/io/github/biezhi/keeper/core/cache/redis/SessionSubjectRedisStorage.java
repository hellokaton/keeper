package io.github.biezhi.keeper.core.cache.redis;

import io.github.biezhi.keeper.core.subject.SessionSubject;
import io.github.biezhi.keeper.core.subject.Subject;
import io.github.biezhi.keeper.utils.JsonUtil;
import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * @author biezhi
 * @date 2019-04-07
 */
public class SessionSubjectRedisStorage extends RedisCache<Subject> {

    public SessionSubjectRedisStorage(StringRedisTemplate stringRedisTemplate) {
        this(stringRedisTemplate, "keeper:subject:");
    }

    public SessionSubjectRedisStorage(StringRedisTemplate stringRedisTemplate, String prefix) {
        super(stringRedisTemplate, prefix);
    }

    @Override
    public Subject get(String key) {
        String json = stringRedisTemplate.opsForValue().get(prefix + key);
        return JsonUtil.toBean(json, SessionSubject.class);
    }

}
