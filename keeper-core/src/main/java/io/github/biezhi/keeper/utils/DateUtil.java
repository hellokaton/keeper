package io.github.biezhi.keeper.utils;

import lombok.experimental.UtilityClass;

import java.util.Date;

/**
 * DateUtil
 *
 * @author biezhi
 * @since 2019/4/9
 */
@UtilityClass
public class DateUtil {

    public Date plus(long millis){
        return new Date(System.currentTimeMillis() + millis);
    }

}
