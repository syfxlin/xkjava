/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import me.ixk.framework.registry.after.AfterBeanRegistry;

/**
 * 后置 BindRegistry
 * <p>
 * 在 Bean 绑定后，单例 Bean 加载前执行，一般用于注册一些属性到 Registry 中。
 *
 * @author Otstar Lin
 * @date 2020/10/13 下午 4:03
 */
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface AfterRegistry {
    Class<? extends AfterBeanRegistry>[] value();
}
