package io.github.biezhi.keeper.utils;

import lombok.experimental.UtilityClass;

import java.util.Date;

/**
 * StringUtil
 *
 * @author biezhi
 * @since 2019/4/9
 */
@UtilityClass
public class StringUtil {

    public boolean isEmpty(String value) {
        if (null == value || value.trim().equals("")) {
            return true;
        }
        return false;
    }

}
