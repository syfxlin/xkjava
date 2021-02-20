/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.servlet.annotation.WebInitParam;
import me.ixk.framework.registry.after.ServletComponentRegistry;

/**
 * Servlet
 *
 * @author Otstar Lin
 * @date 2020/10/30 下午 8:15
 */
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Bean
@AfterRegistry(ServletComponentRegistry.class)
public @interface Servlet {
    @AliasFor("url")
    String[] value() default "";

    @AliasFor("value")
    String[] url() default {};

    @AliasFor(annotation = Bean.class, attribute = "name")
    String[] name() default {};

    @AliasFor(annotation = Bean.class, attribute = "type")
    Class<?>[] type() default {};

    int loadOnStartup() default -1;

    boolean asyncSupported() default false;

    WebInitParam[] initParams() default {};
}
