/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import me.ixk.framework.conditional.OnPropertyCondition;

/**
 * 条件注解（OnProperty）
 * <p>
 * 当配置文件中的值存在并符合指定值时操作
 * <p>
 * 若设置了 havingValue，则需要配置项的值一样的时候才会操作
 * <p>
 * matchIfMissing 若为 true，则在未找到配置项的时候不会抛出异常
 *
 * @author Otstar Lin
 * @date 2020/10/13 下午 4:39
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE, ElementType.METHOD })
@Documented
@Conditional({ OnPropertyCondition.class })
@Order(Order.HIGHEST_PRECEDENCE + 2)
public @interface ConditionalOnProperty {
    @AliasFor("name")
    String[] value() default {  };

    String prefix() default "";

    @AliasFor("value")
    String[] name() default {  };

    String havingValue() default "";

    boolean matchIfMissing() default false;
}
