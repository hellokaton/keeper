package io.github.biezhi.keeper.core.cache;

import io.github.biezhi.keeper.core.authc.AuthorizeInfo;

/**
 * @author biezhi
 * @date 2019-04-07
 */
public interface AuthorizeCache {

    AuthorizeInfo getAuthorizeInfo(String username);

    boolean exists(String username);

    void remove(String username);

    void clearAll();

    void put(String username, AuthorizeInfo authorizeInfo);

}
