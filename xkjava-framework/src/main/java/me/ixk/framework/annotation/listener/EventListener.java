package me.ixk.framework.annotation.listener;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import me.ixk.framework.annotation.core.AliasFor;
import me.ixk.framework.event.ApplicationEvent;

/**
 * 事件注解
 *
 * @author Otstar Lin
 * @date 2021/2/20 下午 9:19
 */
@Target({ ElementType.METHOD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface EventListener {
    @AliasFor("events")
    Class<? extends ApplicationEvent<?>>[] value() default {};

    @AliasFor("value")
    Class<? extends ApplicationEvent<?>>[] events() default {};
}
