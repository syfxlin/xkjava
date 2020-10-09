/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.registrar;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;
import me.ixk.framework.annotations.ScopeType;
import me.ixk.framework.ioc.XkJava;
import me.ixk.framework.utils.MergeAnnotation;

public class MapperScannerRegistrar implements ImportBeanRegistrar {

    @Override
    public void register(
        final XkJava app,
        final Class<?> element,
        final ScopeType scopeType,
        final MergeAnnotation annotation
    ) {
        final ArrayList<String> scanPackages = app.getOrDefaultAttribute(
            "mapperScanPackages",
            new ArrayList<>()
        );
        scanPackages.addAll(Arrays.asList(annotation.get("basePackages")));
        scanPackages.addAll(
            Arrays
                .stream((Class<?>[]) annotation.get("basePackageClasses"))
                .map(Class::getPackageName)
                .collect(Collectors.toList())
        );
    }
}
