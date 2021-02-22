/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.annotation.core;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import me.ixk.framework.conditional.ProfileCondition;

/**
 * 环境选择
 *
 * @author Otstar Lin
 * @date 2020/11/9 上午 10:27
 */
@Target({ ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Conditional({ ProfileCondition.class })
public @interface Profile {
    String PROD = "prod";

    String DEV = "dev";

    String TEST = "test";

    String[] value();
}
