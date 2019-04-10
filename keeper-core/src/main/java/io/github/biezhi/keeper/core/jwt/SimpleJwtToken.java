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
import io.github.biezhi.keeper.Keeper;
import io.github.biezhi.keeper.core.cache.Cache;
import io.github.biezhi.keeper.core.config.JwtConfig;
import io.github.biezhi.keeper.exception.KeeperException;
import io.github.biezhi.keeper.utils.DateUtil;
import io.github.biezhi.keeper.utils.SpringContextUtil;
import io.github.biezhi.keeper.utils.StringUtil;
import io.github.biezhi.keeper.utils.WebUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.Map;
import java.util.Optional;

import static io.github.biezhi.keeper.keeperConst.LOGOUT_KEY;

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
    public String create(String username, Map<String, Object> claims) {
        JWTCreator.Builder builder = JWT.create()
                .withSubject(username)
                .withIssuedAt(new Date())
                .withExpiresAt(DateUtil.plus(config.getExpires().toMillis()));

        if (null != config.getRenewExpires() &&
                config.getRenewExpires().toMillis() > 0) {

            builder.withClaim(REFRESH_EXPIRES_AT,
                    DateUtil.plus(config.getRenewExpires().toMillis()));
        }

        if (null != claims && !claims.isEmpty()) {
            claims.forEach((key, value) -> {
                addClaim(builder, key, value);
            });
        }

        return builder.sign(Algorithm.HMAC256(config.getSecret()));
    }

    private void addClaim(JWTCreator.Builder builder, String key, Object value) {
        if (null == key || null == value) {
            return;
        }
        if (value instanceof String) {
            builder.withClaim(key, (String) value);
        }
        if (value instanceof Boolean) {
            builder.withClaim(key, (Boolean) value);
        }
        if (value instanceof Long) {
            builder.withClaim(key, (Long) value);
        }
        if (value instanceof Integer) {
            builder.withClaim(key, (Integer) value);
        }
        if (value instanceof Double) {
            builder.withClaim(key, (Double) value);
        }
        if (value instanceof Date) {
            builder.withClaim(key, (Date) value);
        }
    }

    @Override
    public String getUsername(String token) {
        if (StringUtil.isEmpty(token)) {
            return null;
        }
        return this.parseToken(token)
                .map(DecodedJWT::getSubject)
                .orElse(null);
    }

    public Cache<String, String> logoutCache() {
        return SpringContextUtil.getBean(Keeper.class).getLogoutCache();
    }

    @Override
    public boolean isExpired(String token) {
        if (StringUtil.isEmpty(token)) {
            return true;
        }

        String sign = token.substring(token.lastIndexOf(".") + 1);
        String key  = String.format(LOGOUT_KEY, sign);
        if (logoutCache().exists(key)) {
            return true;
        }

        Date expiresAt = this.parseToken(token)
                .map(DecodedJWT::getExpiresAt)
                .orElseThrow(() ->
                        new KeeperException("Invalid token type, missing expires_at claim")
                );

        return expiresAt.before(new Date());
    }

    @Override
    public boolean canRenew(String token) {
        if (StringUtil.isEmpty(token)) {
            return false;
        }

        String sign = token.substring(token.lastIndexOf(".") + 1);
        String key  = String.format(LOGOUT_KEY, sign);
        if (logoutCache().exists(key)) {
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
        if (StringUtil.isEmpty(authorization) ||
                !authorization.contains(config.getTokenHead())) {
            return null;
        }
        return authorization.replace(config.getTokenHead(), "");
    }

    @Override
    public Duration getRenewExpire(String token) {
        if (StringUtil.isEmpty(token)) {
            return Duration.ofMillis(0);
        }
        return this.parseToken(token)
                .map(decode -> decode.getClaim(REFRESH_EXPIRES_AT))
                .map(Claim::asLong)
                .map(Duration::ofSeconds)
                .orElse(Duration.ofMillis(0));
    }

    @Override
    public String refresh(String username, Map<String, Object> claims) {
        HttpServletRequest  request  = WebUtil.currentRequest();
        HttpServletResponse response = WebUtil.currentResponse();

        if (null == request || null == response) {
            return null;
        }

        String token = create(username, claims);
        request.setAttribute(NEW_TOKEN, token);
        response.setHeader(config.getHeader(), token);
        return token;
    }

    private Optional<DecodedJWT> parseToken(String token) {
        try {
            return Optional.of(JWT.decode(token));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

}
