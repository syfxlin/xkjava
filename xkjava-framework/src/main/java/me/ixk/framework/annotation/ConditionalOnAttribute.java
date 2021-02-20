/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import me.ixk.framework.conditional.OnAttributeCondition;

/**
 * 条件注解（OnAttribute）
 * <p>
 * 当 Attribute 都存在时进行操作
 *
 * @author Otstar Lin
 * @date 2020/10/13 下午 4:32
 */
@Target({ ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Conditional({ OnAttributeCondition.class })
@Order(Order.LOWEST_PRECEDENCE)
public @interface ConditionalOnAttribute {
    @AliasFor("name")
    String[] value() default {  };

    @AliasFor("value")
    String[] name() default {  };
}
