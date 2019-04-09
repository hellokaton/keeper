package io.github.biezhi.keeper.core.authc;

import io.github.biezhi.keeper.core.authc.cipher.Cipher;
import io.github.biezhi.keeper.core.authc.cipher.EqualsCipher;
import io.github.biezhi.keeper.core.authc.cipher.Md5Cipher;
import io.github.biezhi.keeper.exception.KeeperException;

/**
 * Authentication
 *
 * @author biezhi
 * @date 2019-04-09
 */
public interface Authentication {

    /**
     * User authentication is performed,
     * and the method is only used to load the authentication information without authentication
     *
     * @param token login information
     * @return return the identity information to be stored after successful login,
     * either in session or in a custom cache
     * @throws KeeperException thrown when the login fails or an exception occurs
     */
    AuthenticInfo doAuthentic(AuthorToken token) throws KeeperException;

    /**
     * The method of password verification after loading the identity information
     *
     * @return
     * @see EqualsCipher
     * @see Md5Cipher
     */
    default Cipher cipher() {
        return null;
    }

}
