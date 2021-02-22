/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.annotation.condition;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import me.ixk.framework.annotation.core.Conditional;
import me.ixk.framework.annotation.core.Order;
import me.ixk.framework.conditional.OnExpressionCondition;

/**
 * 条件注解（OnExpression）
 * <p>
 * 当表达式为 true 的时候进行操作
 *
 * @author Otstar Lin
 * @date 2020/10/13 下午 4:35
 */
@Target({ ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Conditional({ OnExpressionCondition.class })
@Order(Order.LOWEST_PRECEDENCE - 1)
public @interface ConditionalOnExpression {
    String value() default "true";
}
