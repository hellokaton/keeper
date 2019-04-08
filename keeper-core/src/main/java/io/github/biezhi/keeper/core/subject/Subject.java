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
import io.github.biezhi.keeper.core.authc.AuthenticInfo;
import io.github.biezhi.keeper.core.authc.AuthorToken;

/**
 * @author biezhi
 * @date 2019-04-05
 */
public interface Subject {

    /**
     * @return returns the current login information
     */
    AuthenticInfo authenticInfo();

    AuthenticInfo login(AuthorToken token);

    void logout();

    /**
     * Refresh user authorization information
     * <p>
     * the authorization information is reloaded after this operation
     */
    void refreshAuthorize();

    /**
     * @return return the login status of the current user
     */
    boolean isLogin();

    /**
     * Token renewal
     *
     * @return
     */
    boolean renew();

    /**
     * Determine if the user has execute permission
     *
     * @param roles       roles currently allowed to execute
     * @param permissions permissions currently allowed to access
     * @return return whether the current user has permission to execute
     */
    boolean hasPermissions(Roles roles, Permissions permissions);

}
