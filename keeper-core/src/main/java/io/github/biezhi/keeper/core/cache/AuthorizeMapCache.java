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
 * @author biezhi
 * @date 2019-04-07
 */
public class AuthorizeMapCache extends MapCache<String, AuthorizeInfo> implements AuthorizeCache {

    @Override
    public AuthorizeInfo getAuthorizeInfo(String username) {
        if (exists(username)) {
            return super.get(username);
        }
        return null;
    }

    @Override
    public boolean exists(String username) {
        return super.exists(username);
    }

    @Override
    public void remove(String username) {
        super.remove(username);
    }

    @Override
    public void clearAll() {
        super.clear();
    }

    @Override
    public void put(String username, AuthorizeInfo authorizeInfo) {
        super.put(username, authorizeInfo);
    }

}
