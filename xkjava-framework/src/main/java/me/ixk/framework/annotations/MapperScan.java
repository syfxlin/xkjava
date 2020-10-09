/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import me.ixk.framework.registrar.MapperScannerRegistrar;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Import(MapperScannerRegistrar.class)
@Bean
public @interface MapperScan {
    @AliasFor("basePackages")
    String[] value() default {  };

    @AliasFor("value")
    String[] basePackages() default {  };

    Class<?>[] basePackageClasses() default {  };
}
