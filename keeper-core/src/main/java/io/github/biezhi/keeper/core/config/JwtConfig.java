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
package io.github.biezhi.keeper.core.config;

import lombok.Data;

import java.time.Duration;

/**
 * jwt configuration
 *
 * @author biezhi
 * @date 2019-04-05
 */
@Data
public class JwtConfig {

    /**
     * Read the token field from the Http Header
     */
    private String header = "Authorization";

    /**
     * token prefix，@link{https://jwt.io/introduction/}
     */
    private String tokenHead = "Bearer ";

    /**
     * The secret when jwt is signed, be sure to configure, do not leak
     */
    private String secret = "keeper";

    /**
     * The generated token is valid.
     * If the refresh time is not set after expiration, you need to log in again.
     */
    private Duration expires = Duration.ofMinutes(10);

    /**
     * The token can be renew at the latest,
     * in which an expired token can be refreshed to generate a new token.
     * <p>
     * If the time is exceeded, re-authentication is required.
     */
    private Duration renewExpires;

}
