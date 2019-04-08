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
import io.github.biezhi.keeper.core.authc.AuthorToken;
import io.github.biezhi.keeper.utils.SpringContextUtil;
import io.github.biezhi.keeper.utils.WebUtil;
import io.github.biezhi.keeper.keeperConst;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.servlet.http.HttpSession;

/**
 * @author biezhi
 * @date 2019-04-05
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SessionSubject extends SimpleSubject {

    @Override
    public String login(AuthorToken token) {
        super.login(token);
        HttpSession session = WebUtil.currentSession(true);
        if (null != session) {
            session.setAttribute(keeperConst.KEEPER_SESSION_KEY, token);
            Keeper keeper = SpringContextUtil.getBean(Keeper.class);
            keeper.addSubject(session.getId(), this, null);
        }
        return null;
    }

    @Override
    public boolean isLogin() {
        HttpSession session = WebUtil.currentSession();
        return null != session && null != session.getAttribute(keeperConst.KEEPER_SESSION_KEY);
    }

    @Override
    public boolean renew() {
        return false;
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
