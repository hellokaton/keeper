package io.github.biezhi.keeper.utils;

import lombok.experimental.UtilityClass;

/**
 * StringUtil
 *
 * @author biezhi
 * @since 2019/4/9
 */
@UtilityClass
public class StringUtil {

    public boolean isEmpty(String value) {
        if (null == value || "".equals(value.trim())) {
            return true;
        }
        return false;
    }

    public boolean isNotEmpty(String value) {
        return null != value && "".equals(value.trim());
    }

}
