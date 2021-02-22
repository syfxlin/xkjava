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
import me.ixk.framework.conditional.OnResourceCondition;

/**
 * 条件注解（OnResource）
 * <p>
 * 当资源存在的时候操作
 *
 * @author Otstar Lin
 * @date 2020/10/13 下午 4:41
 */
@Target({ ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Conditional({ OnResourceCondition.class })
@Order(Order.HIGHEST_PRECEDENCE + 3)
public @interface ConditionalOnResource {
    String[] resources() default {};
}
