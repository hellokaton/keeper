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

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import io.github.biezhi.keeper.Keeper;
import io.github.biezhi.keeper.core.authc.AuthenticInfo;
import io.github.biezhi.keeper.core.authc.AuthorToken;
import io.github.biezhi.keeper.core.config.SessionConfig;
import io.github.biezhi.keeper.exception.ExpiredException;
import io.github.biezhi.keeper.utils.DateUtil;
import io.github.biezhi.keeper.utils.SpringContextUtil;
import io.github.biezhi.keeper.utils.StringUtil;
import io.github.biezhi.keeper.utils.WebUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Date;

import static io.github.biezhi.keeper.keeperConst.KEEPER_SESSION_KEY;

/**
 * SessionSubject
 *
 * @author biezhi
 * @date 2019-04-05
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SessionSubject extends SimpleSubject {

    @Override
    public AuthenticInfo authenticInfo() {
        if (!isLogin()) {
            return null;
        }
        HttpSession session = WebUtil.currentSession();
        if (null == session) {
            return null;
        }
        return (AuthenticInfo) session.getAttribute(KEEPER_SESSION_KEY);
    }

    @Override
    public AuthenticInfo login(AuthorToken token) {
        HttpSession session = WebUtil.currentSession(true);
        if (null == session) {
            return null;
        }

        AuthenticInfo authenticInfo = super.login(token);

        session.setAttribute(KEEPER_SESSION_KEY, authenticInfo);

        // remember me
        SessionConfig config = sessionConfig();
        if (!token.remember() || null == config.getRenewExpires()) {
            return authenticInfo;
        }

        HttpServletResponse response = WebUtil.currentResponse();

        String cookieToken = generateToken(token.username());

        Cookie cookie = new Cookie(config.getCookieName(), cookieToken);
        cookie.setPath(config.getPath());
        cookie.setSecure(config.isSecure());
        cookie.setHttpOnly(config.isHttpOnly());
        cookie.setMaxAge((int) config.getRenewExpires().toMillis() / 1000);
        if (StringUtil.isNotEmpty(config.getDomain())) {
            cookie.setDomain(config.getDomain());
        }
        response.addCookie(cookie);

        // 存储登录状态，处理注销
        this.recordLogin(token.username(), cookieToken);

        return authenticInfo;
    }

    @Override
    public boolean isLogin() {
        SessionConfig config  = sessionConfig();
        HttpSession   session = WebUtil.currentSession(true);
        if (null == session) {
            return false;
        }
        Object attribute = session.getAttribute(KEEPER_SESSION_KEY);
        if (null != attribute) {
            return true;
        }

        if (config.getRenewExpires() != null) {
            throw ExpiredException.build();
        }
        return false;
    }

    @Override
    public boolean renew() {
        HttpSession session = WebUtil.currentSession();

        String token  = "";
        Cookie cookie = getRenewCookie();
        if (null != cookie) {
            token = cookie.getValue();
        }

        if (StringUtil.isEmpty(token)) {
            return false;
        }
        String username = getUsername(token);
        if (StringUtil.isEmpty(username)) {
            return false;
        }

        // token 被撤销，如注销
        if (this.tokenBeRevoked(token, username)) {
            return false;
        }

        AuthenticInfo authenticInfo = authentication().doAuthentic(() -> username);
        session.setAttribute(KEEPER_SESSION_KEY, authenticInfo);
        return true;
    }

    @Override
    public void logout() {
        HttpSession session = WebUtil.currentSession();
        if (null == session) {
            return;
        }
        session.removeAttribute(KEEPER_SESSION_KEY);

        HttpServletResponse response = WebUtil.currentResponse();

        Cookie cookie = getRenewCookie();
        if (null == cookie) {
            return;
        }
        String token = cookie.getValue();
        cookie.setValue("");
        cookie.setMaxAge(-1);
        response.addCookie(cookie);

        String username = getUsername(token);
        if (StringUtil.isEmpty(username)) {
            return;
        }

        // 修改当前 token 的过期时间
        String loginTokenKey = String.format("keeper:login:%s:%s", username, token.substring(token.lastIndexOf(".") + 1));
        keeperCache().set(loginTokenKey, System.currentTimeMillis() / 1000 + "");

    }

    private SessionConfig sessionConfig() {
        return SpringContextUtil.getBean(Keeper.class).getSessionConfig();
    }

    private Cookie getRenewCookie() {
        HttpServletRequest request = WebUtil.currentRequest();
        Cookie[]           cookies = request.getCookies();
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals(sessionConfig().getCookieName())) {
                return cookie;
            }
        }
        return null;
    }

    private String generateToken(String username) {
        SessionConfig config = sessionConfig();
        JWTCreator.Builder builder = JWT.create()
                .withSubject(username)
                .withIssuedAt(new Date())
                .withExpiresAt(DateUtil.plus(config.getRenewExpires().toMillis()));

        return builder.sign(Algorithm.HMAC256(config.getSecret()));
    }

    private String getUsername(String token) {
        if (StringUtil.isEmpty(token)) {
            return null;
        }
        SessionConfig config = sessionConfig();
        try {
            JWTVerifier verifier = JWT.require(
                    Algorithm.HMAC256(config.getSecret()))
                    .build();

            DecodedJWT jwt = verifier.verify(token);
            return jwt.getSubject();
        } catch (Exception e) {
            return null;
        }
    }

}
