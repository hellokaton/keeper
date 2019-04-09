/**
 * Copyright (c) 2019, biezhi (biezhi.me@gmail.com)
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.biezhi.keeper.core.cache.redis;

import io.github.biezhi.keeper.core.authc.AuthorizeInfo;
import io.github.biezhi.keeper.core.authc.impl.SimpleAuthorizeInfo;
import io.github.biezhi.keeper.core.cache.AuthorizeCache;
import io.github.biezhi.keeper.utils.JsonUtil;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.time.Duration;

/**
 * @author biezhi
 * @date 2019-04-07
 */
public class AuthorizeRedisCache extends RedisCache<AuthorizeInfo> implements AuthorizeCache {

    private Duration expiresTime;

    public AuthorizeRedisCache(StringRedisTemplate stringRedisTemplate) {
        this(stringRedisTemplate, "keeper:authorize:");
    }

    public AuthorizeRedisCache(StringRedisTemplate stringRedisTemplate, Duration expiresTime) {
        this(stringRedisTemplate, "keeper:authorize:");
        this.expiresTime = expiresTime;
    }

    public AuthorizeRedisCache(StringRedisTemplate stringRedisTemplate, String prefix) {
        super(stringRedisTemplate, prefix);
    }

    @Override
    public AuthorizeInfo get(String key) {
        String value = stringRedisTemplate.opsForValue().get(prefix + key);
        return JsonUtil.toBean(value, SimpleAuthorizeInfo.class);
    }

    @Override
    public AuthorizeInfo getAuthorizeInfo(String username) {
        if (cached(username)) {
            return this.get(username);
        }
        return null;
    }

    @Override
    public boolean cached(String username) {
        return super.exists(username);
    }

    @Override
    public void remove(String username) {
        super.remove(username);
    }

    @Override
    public void clear() {
        super.clear();
    }

    @Override
    public void set(String username, AuthorizeInfo authorizeInfo) {
        super.set(username, authorizeInfo, expiresTime);
    }

}
