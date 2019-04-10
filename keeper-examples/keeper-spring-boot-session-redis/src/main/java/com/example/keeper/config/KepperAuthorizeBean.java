package com.example.keeper.config;

import com.example.keeper.model.User;
import com.example.keeper.service.UserService;
import io.github.biezhi.keeper.core.authc.*;
import io.github.biezhi.keeper.core.authc.cipher.Cipher;
import io.github.biezhi.keeper.core.authc.impl.SimpleAuthenticInfo;
import io.github.biezhi.keeper.core.authc.impl.SimpleAuthorizeInfo;
import io.github.biezhi.keeper.core.cache.AuthorizeCache;
import io.github.biezhi.keeper.exception.KeeperException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.time.Duration;
import java.util.Set;

@Slf4j
public class KepperAuthorizeBean implements Authentication, Authorization {

    private UserService    userService;
    private AuthorizeCache authorizeCache;

    public KepperAuthorizeBean(UserService userService, StringRedisTemplate stringRedisTemplate) {
        this.userService = userService;
//        this.authorizeCache = new AuthorizeRedisCache(stringRedisTemplate, Duration.ofMinutes(10));
    }

    @Override
    public AuthenticInfo doAuthentic(AuthorToken token) throws KeeperException {
        log.info("doAuthentic :: {}", token.username());

        User user = userService.findByUsername(token.username());

        return new SimpleAuthenticInfo(
                user.getUsername(),
                user.getPassword(),
                user
        );
    }

    @Override
    public Cipher cipher() {
        return Cipher.EQUALS;
    }

    @Override
    public AuthorizeInfo doAuthorization(AuthenticInfo token) throws KeeperException {
        String username = token.username();

        log.info("doAuthorization :: {}", token.username());

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
