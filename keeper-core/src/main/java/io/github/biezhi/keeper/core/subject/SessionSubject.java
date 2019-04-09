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
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import io.github.biezhi.keeper.Keeper;
import io.github.biezhi.keeper.core.authc.AuthenticInfo;
import io.github.biezhi.keeper.core.authc.Authentication;
import io.github.biezhi.keeper.core.authc.AuthorToken;
import io.github.biezhi.keeper.core.authc.cipher.Cipher;
import io.github.biezhi.keeper.core.config.SessionConfig;
import io.github.biezhi.keeper.exception.ExpiredException;
import io.github.biezhi.keeper.exception.WrongPasswordException;
import io.github.biezhi.keeper.keeperConst;
import io.github.biezhi.keeper.utils.DateUtil;
import io.github.biezhi.keeper.utils.SpringContextUtil;
import io.github.biezhi.keeper.utils.WebUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Date;

/**
 * SessionSubject
 *
 * @author biezhi
 * @date 2019-04-05
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class SessionSubject extends SimpleSubject {

    public SessionSubject(AuthenticInfo authenticInfo) {
        super(authenticInfo);
    }

    @Override
    public AuthenticInfo login(AuthorToken token) {
        super.login(token);
        HttpSession session = WebUtil.currentSession(true);
        if (null == session) {
            return null;
        }

        this.authenticInfo = authentication().doAuthentic(token);

        Cipher cipher = authentication().cipher();

        if (null != cipher && !cipher.verify(
                token.password(), authenticInfo.password())) {
            throw WrongPasswordException.build();
        }

        session.setAttribute(keeperConst.KEEPER_SESSION_KEY, authenticInfo);

        // remember me
        SessionConfig config = sessionConfig();
        if (token.remember() && null != config.getRenewExpires()) {
            HttpServletResponse response = WebUtil.currentResponse();

            String kidValue = generateToken(token.username());

            Cookie cookie = new Cookie(config.getCookieName(), kidValue);
            cookie.setMaxAge((int) config.getRenewExpires().toMillis() / 1000);
            response.addCookie(cookie);
        }

        this.isLogin = true;

        return authenticInfo;
    }

    @Override
    public boolean isLogin() {
        SessionConfig config = sessionConfig();
        if (!isLogin && config.getRenewExpires() != null) {
            throw ExpiredException.build();
        }
        return isLogin;
    }

    @Override
    public boolean renew() {
        SessionConfig      config  = sessionConfig();
        HttpServletRequest request = WebUtil.currentRequest();
        HttpSession        session = WebUtil.currentSession();

        String   kid     = "";
        Cookie[] cookies = request.getCookies();
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals(config.getCookieName())) {
                kid = cookie.getValue();
                break;
            }
        }

        DecodedJWT decode   = JWT.decode(kid);
        String     username = decode.getSubject();

        this.authenticInfo = authentication().doAuthentic(() -> username);
        session.setAttribute(keeperConst.KEEPER_SESSION_KEY, authenticInfo);
        return true;
    }

    @Override
    public void logout() {
        super.logout();
        HttpSession session = WebUtil.currentSession();
        if (null == session) {
            return;
        }
        session.removeAttribute(keeperConst.KEEPER_SESSION_KEY);

        Keeper keeper = SpringContextUtil.getBean(Keeper.class);
        keeper.removeSubject(session.getId());
    }

    private Authentication authentication() {
        return SpringContextUtil.getBean(Authentication.class);
    }

    private SessionConfig sessionConfig() {
        return SpringContextUtil.getBean(Keeper.class).getSessionConfig();
    }

    private String generateToken(String username) {
        SessionConfig config = sessionConfig();
        JWTCreator.Builder builder = JWT.create()
                .withSubject(username)
                .withIssuedAt(new Date())
                .withExpiresAt(DateUtil.plus(config.getRenewExpires().toMillis()));

        return builder.sign(Algorithm.HMAC256(config.getSecret()));
    }

}
