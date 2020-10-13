/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import me.ixk.framework.registry.ImportBeanRegistry;

/**
 * Import
 * <p>
 * 替换默认 Bean 的绑定方式，比如 Mapper 是接口的，需要使用 SqlSessionManager.getMapper
 * 获取其对象，此时就需要把默认的注入替换掉
 *
 * @author Otstar Lin
 * @date 2020/10/13 下午 4:55
 */
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface Import {
    Class<? extends ImportBeanRegistry> value();
}
