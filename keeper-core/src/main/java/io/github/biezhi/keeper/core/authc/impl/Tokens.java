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
package io.github.biezhi.keeper.core.authc.impl;

import io.github.biezhi.keeper.core.authc.AuthorToken;
import lombok.experimental.UtilityClass;

/**
 * Tokens
 * <p>
 * Builder for creating {@link AuthorToken}
 *
 * @author biezhi
 * @date 2019-04-07
 */
@UtilityClass
public class Tokens {

    public static AuthorTokenBuilder create(String username) {
        AuthorTokenBuilder builder = new AuthorTokenBuilder();
        builder.username = username;
        return builder;
    }

    public static SimpleToken build(AuthorToken authorToken) {
        SimpleToken simpleToken = new SimpleToken();
        simpleToken.setUsername(authorToken.username());
        simpleToken.setPassword((authorToken.password()));
        return simpleToken;
    }

    public static class AuthorTokenBuilder {
        String username;
        String password;

        public AuthorTokenBuilder password(String password) {
            this.password = password;
            return this;
        }

        public AuthorToken build() {
            SimpleToken simpleToken = new SimpleToken();
            simpleToken.setUsername(username);
            simpleToken.setPassword(password);
            return simpleToken;
        }

    }

}
