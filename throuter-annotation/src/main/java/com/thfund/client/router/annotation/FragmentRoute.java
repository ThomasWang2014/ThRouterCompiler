package com.thfund.client.router.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author WayneWang
 * @since 2017/6/1 10:54
 */

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.CLASS)
public @interface FragmentRoute {
    String routeKey();

    String routeParent();
}
