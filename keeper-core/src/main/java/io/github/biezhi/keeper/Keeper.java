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
package io.github.biezhi.keeper;

import io.github.biezhi.keeper.core.authc.Authorization;
import io.github.biezhi.keeper.core.cache.Cache;
import io.github.biezhi.keeper.core.cache.MapCache;
import io.github.biezhi.keeper.core.config.JwtConfig;
import io.github.biezhi.keeper.core.jwt.JwtToken;
import io.github.biezhi.keeper.core.subject.JwtSubject;
import io.github.biezhi.keeper.core.subject.SessionSubject;
import io.github.biezhi.keeper.core.subject.Subject;
import io.github.biezhi.keeper.enums.SubjectType;
import io.github.biezhi.keeper.utils.SpringContextUtil;
import io.github.biezhi.keeper.utils.WebUtil;
import lombok.Getter;
import lombok.Setter;

import javax.servlet.http.HttpSession;

import java.time.Duration;

/**
 * @author biezhi
 * @date 2019-04-04
 */
public class Keeper {

    private boolean enableURIAuthorizeCache;

    @Setter
    private SubjectType subjectType = SubjectType.SESSION;

    @Setter
    @Getter
    private Authorization authorization;

    @Getter
    @Setter
    private JwtConfig jwtConfig;

    @Setter
    private Cache<String, Subject> subjectStorage = new MapCache<>();

    private static final SessionSubject NO_LOGIN_SESSION_SUBJECT = new SessionSubject();

    public static Subject getSubject() {
        Keeper keeper = SpringContextUtil.getBean(Keeper.class);

        if (SubjectType.SESSION.equals(keeper.subjectType)) {
            HttpSession session = WebUtil.currentSession(true);
            if (null == session || null == session.getAttribute(keeperConst.KEEPER_SESSION_KEY)) {
                return new SessionSubject();
            }
            if (!keeper.subjectStorage.exists(session.getId())) {
                return new SessionSubject();
            }
            return keeper.subjectStorage.get(session.getId());
        } else if (SubjectType.JWT.equals(keeper.subjectType)) {
            JwtToken jwtToken = SpringContextUtil.getBean(JwtToken.class);
            String token = jwtToken.getAuthToken();
            String username = jwtToken.getUsername(token);
            if (null == username) {
                return new JwtSubject();
            }
            if (!keeper.subjectStorage.exists(username)) {
                return new JwtSubject();
            }
            return keeper.subjectStorage.get(username);
        } else {
            return NO_LOGIN_SESSION_SUBJECT;
        }
    }

    public void addSubject(String key, Subject subject, Duration expiresTime) {
        subjectStorage.put(key, subject, expiresTime);
    }

    public void removeSubject(String key) {
        subjectStorage.remove(key);
    }

    public boolean existsSubject(String key) {
        return subjectStorage.exists(key);
    }

    public boolean enableURIAuthorizeCache() {
        return enableURIAuthorizeCache;
    }

    public void enableURIAuthorizeCache(boolean enableURIAuthorizeCache) {
        this.enableURIAuthorizeCache = enableURIAuthorizeCache;
    }

}
