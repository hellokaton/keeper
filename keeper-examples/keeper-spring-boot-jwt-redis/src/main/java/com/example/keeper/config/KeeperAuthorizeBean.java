package com.example.keeper.config;

import com.example.keeper.service.UserService;
import io.github.biezhi.keeper.core.authc.*;
import io.github.biezhi.keeper.core.authc.impl.SimpleAuthorizeInfo;
import io.github.biezhi.keeper.core.cache.AuthorizeCache;
import io.github.biezhi.keeper.core.cache.redis.AuthorizeRedisCache;
import io.github.biezhi.keeper.exception.KeeperException;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.time.Duration;
import java.util.Set;

public class KeeperAuthorizeBean implements Authorization {

    private AuthorizeCache authorizeCache;
    private UserService userService;

    public KeeperAuthorizeBean(UserService userService, StringRedisTemplate stringRedisTemplate) {
        this.userService = userService;
        authorizeCache = new AuthorizeRedisCache(stringRedisTemplate, Duration.ofMinutes(10));
    }

    @Override
    public AuthorizeInfo doAuthorization(AuthorToken token) throws KeeperException {
        String username = token.username();

        Set<String> roles       = userService.findRoles(username);
        Set<String> permissions = userService.findPermissions(username);

        SimpleAuthorizeInfo simpleAuthorizeInfo = new SimpleAuthorizeInfo();
        simpleAuthorizeInfo.setRoles(roles);
        simpleAuthorizeInfo.setPermissions(permissions);
        return simpleAuthorizeInfo;
    }

    @Override
    public AuthorizeCache loadWithCache() {
        return authorizeCache;
    }

}
