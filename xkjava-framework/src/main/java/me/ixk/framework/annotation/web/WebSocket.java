package me.ixk.framework.annotation.web;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import me.ixk.framework.annotation.core.AfterRegistry;
import me.ixk.framework.annotation.core.AliasFor;
import me.ixk.framework.registry.after.RouteRegistry;

/**
 * WebSocket
 *
 * @author Otstar Lin
 * @date 2021/2/22 下午 5:06
 */
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@AfterRegistry(RouteRegistry.class)
public @interface WebSocket {
    @AliasFor("path")
    String[] value() default {};

    @AliasFor("value")
    String[] path() default {};
}
