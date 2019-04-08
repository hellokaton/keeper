package io.github.biezhi.keeper.core.authc;

import java.util.Map;

/**
 * @author biezhi
 * @date 2019-04-09
 */
public interface AuthenticInfo {

    String username();

    String password();

    /**
     * Payload information when logging in, optional
     *
     * @return payload
     */
    default Object payload() {
        return null;
    }

    /**
     * Context information to be stored when logging in, optional
     *
     * @return claims
     */
    default Map<String, Object> claims() {
        return null;
    }

}
