package com.thfund.client.router.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author WayneWang
 * @since 2017/5/28 17:51
 */

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.CLASS)
public @interface ActivityRoute {
    String routeKey();

    String bundleID();

    String formerBundleID() default "";

    String formerClassName() default "";
}
