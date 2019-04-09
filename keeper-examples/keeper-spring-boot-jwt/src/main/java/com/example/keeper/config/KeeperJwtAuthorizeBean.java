package com.example.keeper.config;

import com.example.keeper.service.UserService;
import io.github.biezhi.keeper.core.authc.*;
import io.github.biezhi.keeper.core.authc.impl.SimpleAuthorizeInfo;
import io.github.biezhi.keeper.core.cache.AuthorizeCache;
import io.github.biezhi.keeper.core.cache.map.AuthorizeMapCache;
import io.github.biezhi.keeper.exception.KeeperException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class KeeperJwtAuthorizeBean implements Authorization {

    @Autowired
    private UserService userService;

    private AuthorizeCache authorizeCache = new AuthorizeMapCache();

    @Override
    public AuthorizeInfo doAuthorization(AuthenticInfo token) throws KeeperException {
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
