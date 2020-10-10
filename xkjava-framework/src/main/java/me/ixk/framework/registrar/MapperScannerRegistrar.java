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
import me.ixk.framework.annotations.MapperScans;
import me.ixk.framework.annotations.ScopeType;
import me.ixk.framework.ioc.XkJava;
import me.ixk.framework.utils.AnnotationUtils;
import me.ixk.framework.utils.MergeAnnotation;

public class MapperScannerRegistrar implements AttributeRegistrar {

    @Override
    public Object register(
        XkJava app,
        String attributeName,
        AnnotatedElement element,
        ScopeType scopeType,
        MergeAnnotation annotation
    ) {
        final List<String> scanPackages = app.getOrDefaultAttribute(
            attributeName,
            new ArrayList<>()
        );
        if (annotation.hasAnnotation(MapperScans.class)) {
            for (MapperScan mapperScan : (MapperScan[]) annotation.get(
                MapperScans.class,
                "value"
            )) {
                MergeAnnotation clone = AnnotationUtils.cloneAnnotation(
                    annotation
                );
                clone.addAnnotation(mapperScan);
                this.processMapper(scanPackages, clone);
            }
        }
        if (annotation.hasAnnotation(MapperScan.class)) {
            this.processMapper(scanPackages, annotation);
        }
        return scanPackages;
    }

    private void processMapper(
        List<String> scanPackages,
        MergeAnnotation annotation
    ) {
        scanPackages.addAll(Arrays.asList(annotation.get("basePackages")));
        scanPackages.addAll(
            Arrays
                .stream((Class<?>[]) annotation.get("basePackageClasses"))
                .map(Class::getPackageName)
                .collect(Collectors.toList())
        );
    }
}
