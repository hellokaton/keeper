package io.github.biezhi.keeper.core.authc.cipher;

import io.github.biezhi.keeper.utils.CipherUtil;

import java.util.Objects;

/**
 * CipherMd5
 *
 * @author biezhi
 * @since 2019/4/9
 */
public class CipherMd5 implements Cipher {

    @Override
    public boolean verify(String rawPassword, String encryptPassword) {
        if (null != rawPassword && null != encryptPassword) {
            return Objects.equals(CipherUtil.md5(rawPassword), encryptPassword);
        }
        return false;
    }

}
