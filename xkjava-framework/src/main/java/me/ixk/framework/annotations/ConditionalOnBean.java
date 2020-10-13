/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import me.ixk.framework.conditional.OnBeanCondition;

/**
 * 条件注解（OnBean）
 * <p>
 * 当容器中 Bean 都存在的时候进行操作
 *
 * @author Otstar Lin
 * @date 2020/10/13 下午 4:33
 */
@Target({ ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Conditional({ OnBeanCondition.class })
@Order(Order.LOWEST_PRECEDENCE)
public @interface ConditionalOnBean {
    Class<?>[] value() default {  };

    String[] name() default {  };
}
