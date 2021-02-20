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
 * 条件注解（OnMissingAttribute）
 * <p>
 * 当属性值不存在时进行操作
 *
 * @author Otstar Lin
 * @date 2020/10/13 下午 4:36
 */
@Target({ ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Conditional({ OnAttributeCondition.class })
@Order(Order.LOWEST_PRECEDENCE - 1)
public @interface ConditionalOnMissingAttribute {
    @AliasFor("name")
    String[] value() default {};

    @AliasFor("value")
    String[] name() default {};
}
