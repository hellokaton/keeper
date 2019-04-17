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
package io.github.biezhi.keeper.core.subject;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.github.biezhi.keeper.Keeper;
import io.github.biezhi.keeper.annotation.Permissions;
import io.github.biezhi.keeper.annotation.Roles;
import io.github.biezhi.keeper.core.authc.*;
import io.github.biezhi.keeper.core.authc.cipher.Cipher;
import io.github.biezhi.keeper.core.cache.AuthorizeCache;
import io.github.biezhi.keeper.core.cache.Cache;
import io.github.biezhi.keeper.core.jwt.JwtToken;
import io.github.biezhi.keeper.enums.Logical;
import io.github.biezhi.keeper.exception.UnauthenticException;
import io.github.biezhi.keeper.exception.WrongPasswordException;
import io.github.biezhi.keeper.utils.JsonUtil;
import io.github.biezhi.keeper.utils.SpringContextUtil;
import lombok.Data;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static io.github.biezhi.keeper.keeperConst.KEEPER_AUTHENTIC_KEY;
import static io.github.biezhi.keeper.keeperConst.KEEPER_LOGIN_KEY;

/**
 * @author biezhi
 * @date 2019-04-05
 */
@Data
public abstract class SimpleSubject implements Subject {

    protected Authentication authentication() {
        return SpringContextUtil.getBean(Authentication.class);
    }

    protected JwtToken jwtToken() {
        return SpringContextUtil.getBean(JwtToken.class);
    }

    protected Cache<String, String> keeperCache() {
        return keeper().getKeeperCache();
    }

    protected Keeper keeper() {
        return SpringContextUtil.getBean(Keeper.class);
    }

    @JsonIgnore
    @Override
    public AuthenticInfo login(AuthorToken token) {
        AuthenticInfo authenticInfo = authentication().doAuthentic(token);

        if (null == authenticInfo) {
            throw UnauthenticException.build("AuthenticInfo can not be null.");
        }

        Cipher cipher = authentication().cipher();

        if (null != cipher && !cipher.verify(token, authenticInfo)) {
            throw WrongPasswordException.build();
        }
        return authenticInfo;
    }

    protected void recordLogin(String username, String token) {
        String loginTokenKey = String.format(KEEPER_LOGIN_KEY, username, token.substring(token.lastIndexOf(".") + 1));
        long   createTime    = jwtToken().getCreateTime(token);
        long   expireTime    = jwtToken().getExpireTime(token);

        long seconds = expireTime - (System.currentTimeMillis() / 1000);
        keeperCache().set(loginTokenKey, String.valueOf(createTime), seconds);
    }

    protected void recordLogin(AuthenticInfo authenticInfo, String token) {
        String loginTokenKey = String.format(KEEPER_LOGIN_KEY, authenticInfo.username(), token.substring(token.lastIndexOf(".") + 1));
        long   createTime    = jwtToken().getCreateTime(token);
        long   expireTime    = jwtToken().getExpireTime(token);

        long seconds = expireTime - (System.currentTimeMillis() / 1000);
        keeperCache().set(loginTokenKey, String.valueOf(createTime), seconds);

        String authenticInfoKey = String.format(KEEPER_AUTHENTIC_KEY, authenticInfo.username());
        keeperCache().set(authenticInfoKey, JsonUtil.toJSONString(authenticInfo), seconds);
    }

    protected boolean tokenBeRevoked(String token, String username) {
        String loginTokenKey = String.format(KEEPER_LOGIN_KEY, username, token.substring(token.lastIndexOf(".") + 1));
        if (!keeperCache().exists(loginTokenKey)) {
            return false;
        }
        long tokenCreateTime = jwtToken().getCreateTime(token);

        Long time = keeperCache().get(loginTokenKey, Long.class);
        if (tokenCreateTime == time) {
            return false;
        }
        return true;
    }

    protected void logoutResetCache(String token, String username) {
        long renewExpireTime = jwtToken().getRenewExpireTime(token);

        long seconds = renewExpireTime - System.currentTimeMillis() / 1000;

        // 重置 token 的登录时间，不能删除，因为 token 可能未过期
        String loginTokenKey = String.format(KEEPER_LOGIN_KEY, username, token.substring(token.lastIndexOf(".") + 1));
        keeperCache().set(loginTokenKey, System.currentTimeMillis() / 1000 + "", seconds);

        String authenticInfoKey = String.format(KEEPER_AUTHENTIC_KEY, username);
        keeperCache().remove(authenticInfoKey);
    }

    protected void removeLoginToken(String username, String token) {
        String loginTokenKey = String.format(KEEPER_LOGIN_KEY, username, token.substring(token.lastIndexOf(".") + 1));
        keeperCache().remove(loginTokenKey);
    }

    @JsonIgnore
    @Override
    public boolean hasPermissions(Roles roleAnnotation, Permissions permAnnotation) {
        AuthorizeInfo authorizeInfo = this.authorize(false);
        if (null == authorizeInfo) {
            return false;
        }
        if (null != roleAnnotation && null != authorizeInfo.getRoles()) {
            if (Logical.AND.equals(roleAnnotation.logical())) {
                return rolesAnd(authorizeInfo.getRoles(), roleAnnotation.value());
            }
            if (Logical.OR.equals(roleAnnotation.logical())) {
                String[] value = roleAnnotation.value();
                for (String s : value) {
                    return authorizeInfo.getRoles().contains(s);
                }
            }
        }
        if (null != permAnnotation && null != authorizeInfo.getPermissions()) {
            if (Logical.AND.equals(permAnnotation.logical())) {
                return permissionsAnd(authorizeInfo.getPermissions(), permAnnotation.value());
            }
            if (Logical.OR.equals(permAnnotation.logical())) {
                String[] value = permAnnotation.value();
                for (String s : value) {
                    return authorizeInfo.getPermissions().contains(s);
                }
            }
        }
        return false;
    }

    @JsonIgnore
    @Override
    public void refreshAuthorize() {
        this.authorize(true);
    }

    protected AuthorizeInfo authorize(boolean reload) {
        AuthenticInfo  authenticInfo = this.authenticInfo();
        Authorization  authorization = SpringContextUtil.getBean(Keeper.class).getAuthorization();
        String         username      = authenticInfo.username();
        AuthorizeCache cache         = authorization.loadWithCache();
        if (cache == null) {
            return authorization.doAuthorization(authenticInfo);
        }

        if (reload) {
            cache.remove(username);
        }
        if (!cache.cached(username)) {
            AuthorizeInfo authorizeInfo = authorization.doAuthorization(authenticInfo);
            if (null != authorizeInfo) {
                cache.set(username, authorizeInfo);
            }
            return authorizeInfo;
        } else {
            return cache.getAuthorizeInfo(username);
        }
    }

    private boolean rolesAnd(Set<String> roles, String[] value) {
        List<String> roleList = Arrays.asList(value);
        return roles.containsAll(roleList);
    }

    private boolean permissionsAnd(Set<String> permissions, String[] value) {
        List<String> roleList = Arrays.asList(value);
        return permissions.containsAll(roleList);
    }

}
