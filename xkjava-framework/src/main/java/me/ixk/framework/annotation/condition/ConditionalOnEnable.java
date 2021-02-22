/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.annotation.condition;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import me.ixk.framework.annotation.core.AliasFor;
import me.ixk.framework.annotation.core.Conditional;
import me.ixk.framework.annotation.core.Order;
import me.ixk.framework.conditional.OnEnableCondition;

/**
 * 条件注解（OnEnable）
 *
 * @author Otstar Lin
 * @date 2020/11/30 上午 11:53
 */
@Target({ ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Conditional({ OnEnableCondition.class })
@Order(Order.HIGHEST_PRECEDENCE + 2)
public @interface ConditionalOnEnable {
    @AliasFor("name")
    String[] value() default {};

    @AliasFor("value")
    String[] name() default {};

    Class<?>[] classes() default {};
}
