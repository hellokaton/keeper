package io.github.biezhi.keeper.core.authc.cipher;

/**
 * @author biezhi
 * @since 2019/4/9
 */
public interface Cipher {

    Cipher MD5 = new CipherMd5("");
    Cipher EQUALS = new CipherEquals();

    boolean verify(String rawPassword, String encryptPassword);

}
