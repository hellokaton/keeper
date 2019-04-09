package io.github.biezhi.keeper.core.authc;

import io.github.biezhi.keeper.core.authc.cipher.Cipher;
import io.github.biezhi.keeper.exception.KeeperException;

/**
 * Authentication
 *
 * @author biezhi
 * @date 2019-04-09
 */
public interface Authentication {

    AuthenticInfo doAuthentic(AuthorToken token) throws KeeperException;

    default Cipher cipher() {
        return null;
    }

}
