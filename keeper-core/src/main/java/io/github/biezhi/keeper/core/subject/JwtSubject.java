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
import io.github.biezhi.keeper.Keeper;
import io.github.biezhi.keeper.core.authc.AuthenticInfo;
import io.github.biezhi.keeper.core.authc.AuthorToken;
import io.github.biezhi.keeper.core.authc.impl.SimpleAuthenticInfo;
import io.github.biezhi.keeper.core.jwt.JwtToken;
import io.github.biezhi.keeper.exception.ExpiredException;
import io.github.biezhi.keeper.utils.SpringContextUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author biezhi
 * @date 2019-04-05
 */
@Data
@JsonIgnoreProperties
@EqualsAndHashCode(callSuper = true)
public class JwtSubject extends SimpleSubject {

    @JsonIgnore
    @Override
    public AuthenticInfo login(AuthorToken token) {
        Keeper keeper = SpringContextUtil.getBean(Keeper.class);
        keeper.addSubject(token.username(), this, null);
        String jwtToken = jwtToken().create(token.username());

        SimpleAuthenticInfo authenticInfo = new SimpleAuthenticInfo();
        authenticInfo.setPayload(jwtToken);
        return authenticInfo;
    }

    @JsonIgnore
    @Override
    public void logout() {
        String authToken = jwtToken().getAuthToken();
        String username  = jwtToken().getUsername(authToken);
        if (null == username) {
            return;
        }
        Keeper keeper = SpringContextUtil.getBean(Keeper.class);
        keeper.removeSubject(username);
    }

    @JsonIgnore
    @Override
    public boolean isLogin() {
        String token = jwtToken().getAuthToken();
        if (null == token) {
            return false;
        }

        String username = jwtToken().getUsername(token);
        Keeper keeper   = SpringContextUtil.getBean(Keeper.class);

        if (!keeper.existsSubject(username)) {
            return false;
        }

        boolean expired = jwtToken().isExpired(token);
        if (expired) {
            throw ExpiredException.build();
        }
        return true;
    }

    @Override
    public boolean renew() {
        String token = jwtToken().getAuthToken();
        if (null == token) {
            return false;
        }
        String  username   = jwtToken().getUsername(token);
        boolean canRefresh = jwtToken().canRefresh(token);
        if (canRefresh) {
            String newToken = jwtToken().refresh(username);
            return null != newToken;
        }
        return false;
    }

    private JwtToken jwtToken() {
        return SpringContextUtil.getBean(JwtToken.class);
    }

}
