package io.github.biezhi.keeper.core.authc.cipher;

/**
 * CipherEquals
 *
 * @author biezhi
 * @since 2019/4/9
 */
public class CipherEquals implements Cipher {

    @Override
    public boolean verify(String rawPassword, String encryptPassword) {
        if (null != rawPassword && null != encryptPassword) {
            return rawPassword.equals(encryptPassword);
        }
        return false;
    }

}
