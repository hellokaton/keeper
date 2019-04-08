package io.github.biezhi.keeper.core.authc;

import io.github.biezhi.keeper.exception.KeeperException;

/**
 * @author biezhi
 * @date 2019-04-09
 */
public interface Authentication {

    AuthenticInfo doAuthentic(AuthorToken token) throws KeeperException;

}
