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

import io.github.biezhi.keeper.Keeper;
import io.github.biezhi.keeper.core.authc.AuthenticInfo;
import io.github.biezhi.keeper.core.authc.Authentication;
import io.github.biezhi.keeper.core.authc.AuthorToken;
import io.github.biezhi.keeper.core.config.SessionConfig;
import io.github.biezhi.keeper.exception.ExpiredException;
import io.github.biezhi.keeper.exception.UnauthenticException;
import io.github.biezhi.keeper.utils.SpringContextUtil;
import io.github.biezhi.keeper.utils.WebUtil;
import io.github.biezhi.keeper.keeperConst;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

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
    public AuthenticInfo login(AuthorToken token) {
        super.login(token);
        HttpSession session = WebUtil.currentSession(true);
        if (null == session) {
            return null;
        }

        Authentication authentication = SpringContextUtil.getBean(Authentication.class);
        AuthenticInfo  authenticInfo  = authentication.doAuthentic(token);

        // 只有登录的时候验证密码
        if (!authenticInfo.password().equals(token.password())) {
            throw UnauthenticException.build();
        } else {
            session.setAttribute(keeperConst.KEEPER_SESSION_KEY, authenticInfo);
        }

        // remember me TODO
        SessionConfig config = SpringContextUtil.getBean(Keeper.class).getSessionConfig();
        if (null != config.getRenewExpires() && config.getRenewExpires().toMillis() > 0) {
            HttpServletResponse response = WebUtil.currentResponse();

            String kidValue = "abcdefg" + config.getSecret();

            Cookie cookie = new Cookie(config.getCookieName(), kidValue);
            cookie.setMaxAge((int) config.getRenewExpires().toMillis() / 1000);
            response.addCookie(cookie);
        }

        Keeper keeper = SpringContextUtil.getBean(Keeper.class);
        keeper.addSubject(session.getId(), this, null);
        return authenticInfo;
    }

    @Override
    public boolean isLogin() {
        SessionConfig config = SpringContextUtil.getBean(Keeper.class).getSessionConfig();

        HttpSession session = WebUtil.currentSession();
        boolean     isLogin = null != session && null != session.getAttribute(keeperConst.KEEPER_SESSION_KEY);
        if (!isLogin && config.getRenewExpires() != null) {
            throw ExpiredException.build();
        }
        return isLogin;
    }

    @Override
    public boolean renew() {
        SessionConfig      config  = SpringContextUtil.getBean(Keeper.class).getSessionConfig();
        HttpServletRequest request = WebUtil.currentRequest();

        String   kid     = "";
        Cookie[] cookies = request.getCookies();
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals(config.getCookieName())) {
                kid = cookie.getValue();
                break;
            }
        }

        Authentication authentication = SpringContextUtil.getBean(Authentication.class);

        String kidValue = kid;

        AuthenticInfo authenticInfo = authentication.doAuthentic(() -> kidValue);

        HttpSession session = WebUtil.currentSession();
        session.setAttribute(keeperConst.KEEPER_SESSION_KEY, authenticInfo);
        return true;
    }

    @Override
    public void logout() {
        super.logout();
        HttpSession session = WebUtil.currentSession();
        if (null != session) {
            session.removeAttribute(keeperConst.KEEPER_SESSION_KEY);

            Keeper keeper = SpringContextUtil.getBean(Keeper.class);
            keeper.removeSubject(session.getId());
        }
    }

}
