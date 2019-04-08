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
package io.github.biezhi.keeper.core.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import io.github.biezhi.keeper.core.config.JwtConfig;
import io.github.biezhi.keeper.exception.KeeperException;
import io.github.biezhi.keeper.utils.WebUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.time.Duration;
import java.time.Instant;
import java.util.Calendar;
import java.util.Date;
import java.util.Optional;

public class SimpleJwtToken implements JwtToken {

    /**
     * Token the latest refresh token after expiration.
     * <p>
     * Does not refresh by default, effective when the configuration refresh expires.
     *
     * @see JwtConfig#setRenewExpires(Duration)
     */
    private static final String REFRESH_EXPIRES_AT = "rea";

    /**
     * When the token is renewed, the request is guaranteed to be executed correctly,
     * and the new token is cached in the request attribute
     */
    private static final String NEW_TOKEN = "auth:new_token";

    /**
     * JWT configuration, including key, expiration time, headers, and so on
     *
     * @see JwtConfig
     */
    private final JwtConfig config;

    public SimpleJwtToken(JwtConfig config) {
        this.config = config;
    }

    @Override
    public String create(String username) {
        try {
            JWTCreator.Builder builder = JWT.create()
                    .withSubject(username)
                    .withIssuedAt(new Date())
                    .withExpiresAt(datePlus(config.getExpires().toMillis()));

            if (null != config.getRenewExpires() &&
                    config.getRenewExpires().toMillis() > 0) {

                builder.withClaim(REFRESH_EXPIRES_AT,
                        datePlus(config.getRenewExpires().toMillis()));
            }
            return builder.sign(Algorithm.HMAC256(config.getSecret()));
        } catch (UnsupportedEncodingException e) {
            return null;
        }
    }

    @Override
    public String getUsername(String token) {
        if (null == token) {
            return null;
        }
        return this.parseToken(token)
                .map(DecodedJWT::getSubject)
                .orElse(null);
    }

    @Override
    public boolean isExpired(String token) {
        Date now = Calendar.getInstance().getTime();

        Date expiresAt = this.parseToken(token)
                .map(DecodedJWT::getExpiresAt)
                .orElseThrow(() ->
                        new KeeperException("Invalid token type, missing expires_at claim")
                );

        return expiresAt.before(now);
    }

    @Override
    public boolean canRefresh(String token) {
        if (null == token) {
            return false;
        }

        Long expiresAt = this.parseToken(token)
                .map(decode -> decode.getClaim(REFRESH_EXPIRES_AT))
                .map(Claim::asLong)
                .orElse(0L);

        long now = Instant.now().getEpochSecond();
        return expiresAt > now;
    }

    @Override
    public String getAuthToken() {
        HttpServletRequest request = WebUtil.currentRequest();
        if (null == request) {
            return null;
        }
        if (null != request.getAttribute(NEW_TOKEN)) {
            return (String) request.getAttribute(NEW_TOKEN);
        }
        String authorization = request.getHeader(config.getHeader());
        if (null == authorization) {
            return null;
        }
        return authorization.replace(config.getTokenHead(), "");
    }

    @Override
    public String refresh(String username) {
        HttpServletRequest  request  = WebUtil.currentRequest();
        HttpServletResponse response = WebUtil.currentResponse();

        if (null == request || null == response) {
            return null;
        }

        String token = create(username);
        request.setAttribute(NEW_TOKEN, token);
        response.setHeader(config.getHeader(), token);
        return token;
    }

    private Date datePlus(long millis) {
        return new Date(System.currentTimeMillis() + millis);
    }

    private Optional<DecodedJWT> parseToken(String token) {
        try {
            return Optional.of(JWT.decode(token));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

}
