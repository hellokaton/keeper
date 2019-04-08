package io.github.biezhi.keeper.core.authc;

import lombok.experimental.UtilityClass;

import java.util.HashMap;
import java.util.Map;

/**
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
        simpleToken.setPayload(authorToken.payload());
        simpleToken.setClaims((authorToken.claims()));
        return simpleToken;
    }

    public static class AuthorTokenBuilder {
        String username;
        Object payload;
        int    rememberSeconds;

        Map<String, Object> claims;

        public AuthorTokenBuilder rememberMe(int rememberSeconds) {
            this.rememberSeconds = rememberSeconds;
            return this;
        }

        public AuthorTokenBuilder payload(Object payload) {
            this.payload = payload;
            return this;
        }

        public AuthorTokenBuilder withClaims(Map<String, Object> claims) {
            this.claims = claims;
            return this;
        }

        public AuthorTokenBuilder withClaim(String key, Object value) {
            if (null == claims) {
                claims = new HashMap<>();
            }
            claims.putIfAbsent(key, value);
            return this;
        }

        public AuthorToken build() {
            SimpleToken simpleToken = new SimpleToken();
            simpleToken.setUsername(username);
            simpleToken.setPayload(payload);
            simpleToken.setClaims(claims);
            return simpleToken;
        }

    }

}
