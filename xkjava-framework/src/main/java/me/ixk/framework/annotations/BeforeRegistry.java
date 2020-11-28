/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import me.ixk.framework.registry.before.BeforeBeanRegistry;

/**
 * 前置 BindRegistry
 * <p>
 * 在 Bean 绑定前执行，一般用于注册一些属性到 Registry 中。
 *
 * @author Otstar Lin
 * @date 2020/10/13 下午 4:23
 */
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface BeforeRegistry {
    Class<? extends BeforeBeanRegistry>[] value();
}
