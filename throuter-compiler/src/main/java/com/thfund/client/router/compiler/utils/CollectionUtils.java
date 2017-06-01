package com.thfund.client.router.compiler.utils;

import java.util.Collection;

/**
 * @author WayneWang
 * @since 2017/6/1 17:34
 */

public class CollectionUtils {
    public static boolean isEmpty(final Collection<?> coll) {
        return coll == null || coll.isEmpty();
    }
}
