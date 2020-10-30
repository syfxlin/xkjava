/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import me.ixk.framework.registry.after.ExceptionHandlerRegistry;
import me.ixk.framework.registry.after.InitBinderRegistry;

/**
 * 控制器增强
 * <p>
 * 增强控制器类
 *
 * @author Otstar Lin
 * @date 2020/10/13 下午 4:46
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Lazy
@Bean
@AfterImport({ ExceptionHandlerRegistry.class, InitBinderRegistry.class })
public @interface ControllerAdvice {
    @AliasFor(value = "name", annotation = Bean.class, attribute = "value")
    String[] value() default {  };

    @AliasFor(value = "value", annotation = Bean.class, attribute = "name")
    String[] name() default {  };

    @AliasFor(annotation = Bean.class, attribute = "type")
    Class<?>[] type() default {  };
}
