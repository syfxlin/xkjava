/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import me.ixk.framework.registry.request.RequestAttributeRegistry;

/**
 * 请求 Attribute 注入
 * <p>
 * 效果和 @Attribute 类似，不过该注解设置的是 Request 对象的 Attribute，而不是容器的
 * Attribute。并且只会在请求时运行，是线程安全的。
 *
 * @author Otstar Lin
 * @date 2020/10/13 下午 5:10
 */
@Target({ ElementType.METHOD, ElementType.ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(RequestAttributes.class)
public @interface RequestAttribute {
    @AliasFor("name")
    String value() default "";

    @AliasFor("value")
    String name() default "";

    Class<? extends RequestAttributeRegistry> registry();
}
