/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import me.ixk.framework.registry.attribute.AttributeRegistry;

/**
 * 属性
 * <p>
 * 注解处理器会提取该注解标记元素的信息，同时调用对应的 AttributeRegistry，在 AttributeRegistry 中的返回值会被设置到
 * Container Attribute 中，name 对应 Attribute 的名称。
 *
 * @author Otstar Lin
 * @date 2020/10/13 下午 4:13
 */
@Target({ ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(Attributes.class)
public @interface Attribute {
    @AliasFor("name")
    String value() default "";

    @AliasFor("value")
    String name() default "";

    Class<? extends AttributeRegistry> registry() default AttributeRegistry.class;

    boolean after() default false;
}
