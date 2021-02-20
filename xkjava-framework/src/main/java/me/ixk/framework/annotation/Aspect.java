/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import me.ixk.framework.registry.after.AspectRegistry;

/**
 * 切面
 * <p>
 * pointcut 为 AspectJ 的表达式
 *
 * @author Otstar Lin
 * @date 2020/10/13 下午 4:12
 */
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Bean
@AfterRegistry(AspectRegistry.class)
public @interface Aspect {
    @AliasFor("pointcut")
    String value() default "";

    @AliasFor("value")
    String pointcut() default "";

    @AliasFor(annotation = Bean.class, attribute = "type")
    Class<?>[] type() default {};

    @AliasFor(annotation = Bean.class, attribute = "name")
    String[] name() default {};
}
