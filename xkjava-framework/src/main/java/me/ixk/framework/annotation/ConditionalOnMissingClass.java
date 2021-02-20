/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import me.ixk.framework.conditional.OnClassCondition;

/**
 * 条件注解（OnMissingClass）
 * <p>
 * 当类都不存在时操作
 *
 * @author Otstar Lin
 * @date 2020/10/13 下午 4:38
 */
@Target({ ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Conditional({ OnClassCondition.class })
@Order(Order.HIGHEST_PRECEDENCE + 1)
public @interface ConditionalOnMissingClass {
    Class<?>[] value() default {  };

    String[] name() default {  };
}
