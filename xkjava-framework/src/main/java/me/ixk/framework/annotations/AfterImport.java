/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import me.ixk.framework.registry.after.AfterImportBeanRegistry;

@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface AfterImport {
    Class<? extends AfterImportBeanRegistry>[] value();
}
