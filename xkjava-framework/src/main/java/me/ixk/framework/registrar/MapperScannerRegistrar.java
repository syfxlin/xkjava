/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.registrar;

import java.lang.reflect.AnnotatedElement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import me.ixk.framework.annotations.MapperScan;
import me.ixk.framework.annotations.ScopeType;
import me.ixk.framework.ioc.XkJava;
import me.ixk.framework.utils.MergedAnnotation;

public class MapperScannerRegistrar implements AttributeRegistrar {

    @Override
    public Object register(
        XkJava app,
        String attributeName,
        AnnotatedElement element,
        ScopeType scopeType,
        MergedAnnotation annotation
    ) {
        final List<String> scanPackages = app.getOrDefaultAttribute(
            attributeName,
            new ArrayList<>()
        );
        if (annotation.hasAnnotation(MapperScan.class)) {
            this.processMapper(scanPackages, annotation);
        }
        return scanPackages;
    }

    private void processMapper(
        List<String> scanPackages,
        MergedAnnotation annotation
    ) {
        for (MapperScan scan : annotation.getAnnotations(MapperScan.class)) {
            scanPackages.addAll(Arrays.asList(scan.basePackages()));
            scanPackages.addAll(
                Arrays
                    .stream(scan.basePackageClasses())
                    .map(Class::getPackageName)
                    .collect(Collectors.toList())
            );
        }
    }
}
