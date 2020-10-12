/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import me.ixk.framework.registry.after.WebResolverRegistry;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@AfterImport(WebResolverRegistry.class)
public @interface WebResolver {}
