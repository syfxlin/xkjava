/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import me.ixk.framework.config.PropertyResolver;

/**
 * 属性配置
 * <p>
 * 配合 @ConfigurationProperties 使用
 *
 * @author Otstar Lin
 * @date 2020/11/5 下午 10:29
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Property {
    String EMPTY = "EMPTY";

    @AliasFor("name")
    String value() default "";

    @AliasFor("value")
    String name() default "";

    String defaultValue() default EMPTY;

    Class<? extends PropertyResolver> resolver() default PropertyResolver.class;

    boolean skip() default false;

    /**
     * 当 full 为 true 的时候，此时注入的值就是不加 @ConfigurationPrefix prefix 的值
     *
     * @return 是否为 full 模式
     */
    boolean full() default false;
}
