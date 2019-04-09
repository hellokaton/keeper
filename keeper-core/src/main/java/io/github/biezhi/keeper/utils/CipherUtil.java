package io.github.biezhi.keeper.utils;

import lombok.experimental.UtilityClass;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * CipherUtil
 *
 * @author biezhi
 * @since 2019/4/9
 */
@UtilityClass
public class CipherUtil {

    public String md5(String text) {
        byte[]        data = text.getBytes();
        MessageDigest md5  = null;
        try {
            md5 = MessageDigest.getInstance("MD5");
            md5.update(data);
        } catch (NoSuchAlgorithmException e) {
            return "";
        }
        byte[] resultBytes = md5.digest();

        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < resultBytes.length; i++) {
            if (Integer.toHexString(0xFF & resultBytes[i]).length() == 1) {
                builder.append("0").append(
                        Integer.toHexString(0xFF & resultBytes[i]));
            } else {
                builder.append(Integer.toHexString(0xFF & resultBytes[i]));
            }
        }
        return builder.toString();
    }

}
