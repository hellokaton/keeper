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

import io.github.biezhi.keeper.annotation.Permissions;
import io.github.biezhi.keeper.annotation.Roles;
import io.github.biezhi.keeper.core.authc.AuthorToken;
import io.github.biezhi.keeper.core.authc.SimpleToken;

/**
 * 续期
 * 续约
 *
 * @author biezhi
 * @date 2019-04-05
 */
public interface Subject {

    /**
     * @return returns the unique identity of the user currently logged in,
     * or NULL if the user is not logged in
     */
    String username();

    /**
     * @return returns the current login information
     */
    SimpleToken token();

    String login(AuthorToken token);

    void logout();

    /**
     * Refresh user authorization information
     * <p>
     * the authorization information is reloaded after this operation
     */
    void refreshAuthorize();

    boolean isLogin();

    boolean renew();

    boolean hasPermissions(Roles roles, Permissions permissions);

}
