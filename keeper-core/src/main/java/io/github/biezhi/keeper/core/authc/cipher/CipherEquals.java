package io.github.biezhi.keeper.core.authc.cipher;

import io.github.biezhi.keeper.core.authc.AuthenticInfo;
import io.github.biezhi.keeper.core.authc.AuthorToken;

/**
 * CipherEquals
 *
 * @author biezhi
 * @since 2019/4/9
 */
public class CipherEquals implements Cipher {

    private boolean ignoreCase;

    public CipherEquals() {
        this(false);
    }

    public CipherEquals(boolean ignoreCase) {
        this.ignoreCase = ignoreCase;
    }

    @Override
    public boolean verify(AuthorToken token, AuthenticInfo authenticInfo) {
        String rawPassword     = tokenCipher(token);
        String encryptPassword = authenticCipher(authenticInfo);
        if (null == rawPassword || null == encryptPassword) {
            return false;
        }
        if (ignoreCase) {
            return encryptPassword.equalsIgnoreCase(rawPassword);
        }
        return encryptPassword.equals(rawPassword);
    }

}
