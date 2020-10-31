/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.servlet.DispatcherType;
import javax.servlet.annotation.WebInitParam;
import me.ixk.framework.registry.after.ServletComponentRegistry;

/**
 * 过滤器
 *
 * @author Otstar Lin
 * @date 2020/10/30 上午 11:31
 */
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Bean
@AfterImport(ServletComponentRegistry.class)
public @interface Filter {
    @AliasFor("url")
    String[] value() default "";

    @AliasFor("value")
    String[] url() default {  };

    @AliasFor(annotation = Bean.class, attribute = "name")
    String[] name() default {  };

    @AliasFor(annotation = Bean.class, attribute = "type")
    Class<?>[] type() default {  };

    DispatcherType[] dispatcherTypes() default { DispatcherType.REQUEST };

    WebInitParam[] initParams() default {  };

    boolean asyncSupported() default false;
}
