/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.annotation.web;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import me.ixk.framework.annotation.core.AliasFor;

/**
 * 异常处理器
 * <p>
 * 用于控制路由抛出异常时的处理
 * <p>
 * 默认会处理运行时异常和 Error，如果设置了异常类，则只会处理对应异常或其子类异常
 *
 * @author Otstar Lin
 * @date 2020/10/13 下午 4:50
 */
@Target({ ElementType.METHOD, ElementType.ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface ExceptionHandler {
    @AliasFor("exception")
    Class<? extends Throwable>[] value() default {};

    @AliasFor("value")
    Class<? extends Throwable>[] exception() default {};
}
