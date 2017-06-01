package com.thfund.client.router.compiler.utils;

/**
 * @author WayneWang
 * @since 2017/6/1 17:35
 */

public class StringUtils {
    public static boolean isEmpty(final CharSequence cs) {
        return cs == null || cs.length() == 0;
    }

    public static boolean isNotEmpty(final CharSequence cs) {
        return !isEmpty(cs);
    }
}
