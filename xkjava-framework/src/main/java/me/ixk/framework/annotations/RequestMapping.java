/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import me.ixk.framework.registry.after.RouteRegistry;

@Target({ ElementType.METHOD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@AfterImport(RouteRegistry.class)
public @interface RequestMapping {
    @AliasFor("path")
    String[] value() default {  };

    @AliasFor("value")
    String[] path() default {  };

    RequestMethod[] method() default RequestMethod.GET;
}
