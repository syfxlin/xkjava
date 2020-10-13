/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 跳过注入
 * <p>
 * 在有 @Autowired 或者 Setter 的时候容器就会注入，使用该注解声明容器不注入
 *
 * @author Otstar Lin
 * @date 2020/10/13 下午 5:50
 */
@Target({ ElementType.FIELD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface SkipPropertyAutowired {
}
