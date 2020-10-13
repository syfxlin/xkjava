/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import me.ixk.framework.registry.MapperRegistry;

/**
 * 标记 Mybatis 的 Mapper
 *
 * @author Otstar Lin
 * @date 2020/10/13 下午 5:01
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Scope(type = ScopeType.PROTOTYPE)
@Bean
@Import(MapperRegistry.class)
public @interface Mapper {
    @AliasFor(value = "name", annotation = Bean.class, attribute = "value")
    String[] value() default {  };

    @AliasFor(value = "value", annotation = Bean.class, attribute = "name")
    String[] name() default {  };
}
