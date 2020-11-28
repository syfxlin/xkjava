/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import me.ixk.framework.registry.after.ExceptionHandlerRegistry;
import me.ixk.framework.registry.after.InitBinderRegistry;

/**
 * 控制器
 * <p>
 * 用于定义路由方法的类
 *
 * @author Otstar Lin
 * @date 2020/10/13 下午 4:45
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Lazy
@Bean
@AfterRegistry({ ExceptionHandlerRegistry.class, InitBinderRegistry.class })
public @interface Controller {
    @AliasFor(value = "name", annotation = Bean.class, attribute = "value")
    String[] value() default {};

    @AliasFor(value = "value", annotation = Bean.class, attribute = "name")
    String[] name() default {};

    @AliasFor(annotation = Bean.class, attribute = "type")
    Class<?>[] type() default {};
}
