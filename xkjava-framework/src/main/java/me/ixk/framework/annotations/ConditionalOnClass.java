/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import me.ixk.framework.conditional.OnClassCondition;

/**
 * 条件注解（OnClass）
 * <p>
 * 当类都存在的时候进行操作
 *
 * @author Otstar Lin
 * @date 2020/10/13 下午 4:34
 */
@Target({ ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Conditional({ OnClassCondition.class })
@Order(Order.HIGHEST_PRECEDENCE + 1)
public @interface ConditionalOnClass {
    Class<?>[] value() default {  };

    String[] name() default {  };
}
