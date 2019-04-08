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
package io.github.biezhi.keeper.core.cache;

import io.github.biezhi.keeper.core.authc.AuthorizeInfo;

/**
 * AuthorizeCache
 * <p>
 * Define read and write of authorization information
 *
 * @author biezhi
 * @date 2019-04-07
 */
public interface AuthorizeCache {

    /**
     * Obtain the authorization information of the user from the cache according to the username
     *
     * @param username user unique identifier
     * @return {@link io.github.biezhi.keeper.core.authc.SimpleAuthorizeInfo}
     */
    AuthorizeInfo getAuthorizeInfo(String username);

    /**
     * Whether the user has cached authorization information
     *
     * @param username user unique identifier
     * @return
     */
    boolean cached(String username);

    /**
     * Remove the user authorization cache information
     *
     * @param username user unique identifier
     */
    void remove(String username);

    /**
     * Empty the cache
     */
    void clear();

    /**
     * Write an authorization cache
     *
     * @param username      user unique identifier
     * @param authorizeInfo AuthorizeInfo
     */
    void put(String username, AuthorizeInfo authorizeInfo);

}
