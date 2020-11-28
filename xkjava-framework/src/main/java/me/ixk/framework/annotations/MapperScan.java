/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import me.ixk.framework.registry.after.MapperScannerRegistry;

/**
 * Mybatis Mapper 扫描的包
 *
 * @author Otstar Lin
 * @date 2020/10/13 下午 5:01
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@AfterRegistry(MapperScannerRegistry.class)
@Repeatable(MapperScans.class)
public @interface MapperScan {
    @AliasFor("basePackages")
    String[] value() default {};

    @AliasFor("value")
    String[] basePackages() default {};

    Class<?>[] basePackageClasses() default {};
}
