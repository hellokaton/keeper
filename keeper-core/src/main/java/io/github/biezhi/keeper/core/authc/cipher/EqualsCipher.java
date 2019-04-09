package io.github.biezhi.keeper.core.authc.cipher;

import io.github.biezhi.keeper.core.authc.AuthenticInfo;
import io.github.biezhi.keeper.core.authc.AuthorToken;

/**
 * EqualsCipher
 *
 * @author biezhi
 * @since 2019/4/9
 */
public class EqualsCipher implements Cipher {

    private boolean ignoreCase;

    public EqualsCipher() {
        this(false);
    }

    public EqualsCipher(boolean ignoreCase) {
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
