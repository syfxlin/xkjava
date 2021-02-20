/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import me.ixk.framework.conditional.OnBeanCondition;

/**
 * 条件注解（OnMissingBean）
 * <p>
 * 当 Bean 都不存在时操作
 *
 * @author Otstar Lin
 * @date 2020/10/13 下午 4:37
 */
@Target({ ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Conditional({ OnBeanCondition.class })
@Order(Order.LOWEST_PRECEDENCE - 1)
public @interface ConditionalOnMissingBean {
    Class<?>[] value() default {};

    String[] name() default {};
}
