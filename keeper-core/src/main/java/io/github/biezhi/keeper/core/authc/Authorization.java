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
package io.github.biezhi.keeper.core.authc;

import io.github.biezhi.keeper.core.authc.impl.SimpleAuthorizeInfo;
import io.github.biezhi.keeper.core.cache.AuthorizeCache;
import io.github.biezhi.keeper.exception.KeeperException;

/**
 * Authorization interface
 * <p>
 * Implement the interface when it needs to implement authorization
 * and host it in the Spring container.
 *
 * @author biezhi
 * @date 2019-04-08
 */
public interface Authorization {

    /**
     * Load user authorization information, including roles and permissions sets.
     *
     * @param authenticInfo the authenticated user token identifier, mainly username
     * @return Authorization information {@link SimpleAuthorizeInfo}
     * @throws KeeperException
     */
    AuthorizeInfo doAuthorization(AuthenticInfo authenticInfo) throws KeeperException;

    /**
     * Load authorization information from the cache,
     * loaded by default from {@link Authorization#doAuthorization(AuthenticInfo)}
     *
     * @return Authorization information {@link SimpleAuthorizeInfo}
     */
    default AuthorizeCache loadWithCache() {
        return null;
    }

}
