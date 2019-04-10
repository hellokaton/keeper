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
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.github.biezhi.keeper.core.authc.AuthenticInfo;
import io.github.biezhi.keeper.core.authc.AuthorToken;
import io.github.biezhi.keeper.core.authc.impl.SimpleAuthenticInfo;
import io.github.biezhi.keeper.exception.ExpiredException;
import io.github.biezhi.keeper.utils.JsonUtil;
import io.github.biezhi.keeper.utils.StringUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

/**
 * JwtSubject
 *
 * @author biezhi
 * @date 2019-04-05
 */
@Data
@Slf4j
@JsonIgnoreProperties
@EqualsAndHashCode(callSuper = true)
public class JwtSubject extends SimpleSubject {

    @Override
    public AuthenticInfo authenticInfo() {
        if (!isLogin()) {
            return null;
        }
        String token    = jwtToken().getAuthToken();
        String username = jwtToken().getUsername(token);

        String authenticInfoKey = String.format("keeper:authentic:%s", username);

        if (keeperCache().exists(authenticInfoKey)) {
            return keeperCache().get(authenticInfoKey, SimpleAuthenticInfo.class);
        }
        AuthenticInfo authenticInfo = authentication().doAuthentic(() -> username);
        if (null == authenticInfo) {
            return null;
        }

        keeperCache().set(authenticInfoKey, JsonUtil.toJSONString(authenticInfo));
        return authenticInfo;
    }

    @JsonIgnore
    @Override
    public AuthenticInfo login(AuthorToken token) {
        SimpleAuthenticInfo authenticInfo = (SimpleAuthenticInfo) super.login(token);

        String jwtToken = jwtToken().create(token.username(), authenticInfo.claims());

        // 存储登录状态，处理注销、重置密码、token 过期等问题
        this.recordLogin(token.username(), jwtToken);

        authenticInfo.setPayload(jwtToken);

        // 存储登录成功的信息
        String authenticInfoKey = String.format("keeper:authentic:%s", token.username());
        keeperCache().set(authenticInfoKey, JsonUtil.toJSONString(authenticInfo));
        return authenticInfo;
    }

    @JsonIgnore
    @Override
    public void logout() {
        String token    = jwtToken().getAuthToken();
        String username = jwtToken().getUsername(token);
        if (StringUtil.isEmpty(username)) {
            return;
        }
        this.resetLoginTime(token, username);
    }


    @JsonIgnore
    @Override
    public boolean isLogin() {
        String token = jwtToken().getAuthToken();
        if (StringUtil.isEmpty(token)) {
            return false;
        }

        String username = jwtToken().getUsername(token);
        if (StringUtil.isEmpty(username)) {
            return false;
        }

        // token 被撤销，如注销
        if (this.tokenBeRevoked(token, username)) {
            return false;
        }

        boolean expired = jwtToken().isExpired(token);
        if (expired) {
            this.removeLoginToken(username, token);
            throw ExpiredException.build();
        }
        return true;
    }

    @Override
    public boolean renew() {
        String token = jwtToken().getAuthToken();
        if (StringUtil.isEmpty(token)) {
            return false;
        }
        String  username   = jwtToken().getUsername(token);
        boolean canRefresh = jwtToken().canRenew(token);
        if (!canRefresh) {
            return false;
        }

        AuthenticInfo authenticInfo = authentication().doAuthentic(() -> username);

        String authenticInfoKey = String.format("keeper:authentic:%s", username);
        keeperCache().set(authenticInfoKey, JsonUtil.toJSONString(authenticInfo));

        String newToken = jwtToken().refresh(username, authenticInfo.claims());
        log.info("renew success, token: {}", newToken);

        // 存储登录状态，处理注销、重置密码、token 过期等问题
        this.recordLogin(username, newToken);
        return true;
    }

}
