/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import me.ixk.framework.ioc.binder.DataBinder.Converter;

/**
 * Web 数据绑定
 *
 * @author Otstar Lin
 * @date 2020/10/24 下午 7:13
 */
@Target({ ElementType.PARAMETER, ElementType.ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
@DataBind
public @interface WebBind {
    @AliasFor(value = "name", annotation = DataBind.class, attribute = "value")
    String value() default "";

    @AliasFor(value = "value", annotation = DataBind.class, attribute = "name")
    String name() default "";

    @AliasFor(annotation = DataBind.class, attribute = "required")
    boolean required() default false;

    @AliasFor(annotation = DataBind.class, attribute = "defaultValue")
    String defaultValue() default DataBind.EMPTY;

    Class<? extends Converter>[] converter() default {};

    Type type() default Type.ALL;

    enum Type {
        /**
         * All
         */
        ALL,
        /**
         * Query
         */
        QUERY,
        /**
         * Body
         */
        BODY,
        /**
         * Path
         */
        PATH,
        /**
         * Part
         */
        PART,
        /**
         * Header
         */
        HEADER,
        /**
         * Cookie
         */
        COOKIE,
        /**
         * Session
         */
        SESSION,
        /**
         * Request
         */
        ATTRIBUTE,
    }
}
