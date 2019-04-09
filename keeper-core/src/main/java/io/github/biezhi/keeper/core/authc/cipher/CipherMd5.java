package io.github.biezhi.keeper.core.authc.cipher;

import io.github.biezhi.keeper.core.authc.AuthenticInfo;
import io.github.biezhi.keeper.core.authc.AuthorToken;
import io.github.biezhi.keeper.utils.CipherUtil;

/**
 * CipherMd5
 *
 * @author biezhi
 * @since 2019/4/9
 */
public class CipherMd5 implements Cipher {

    @Override
    public boolean verify(AuthorToken token, AuthenticInfo authenticInfo) {
        String rawPassword     = tokenCipher(token);
        String encryptPassword = authenticCipher(authenticInfo);
        if (null == rawPassword || null == encryptPassword) {
            return false;
        }
        return encryptPassword.equalsIgnoreCase(CipherUtil.md5(rawPassword));
    }

}
