/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.registry.after;

import java.lang.reflect.AnnotatedElement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import me.ixk.framework.annotation.core.Component;
import me.ixk.framework.annotation.database.MapperScan;
import me.ixk.framework.ioc.XkJava;
import me.ixk.framework.util.MergedAnnotation;

/**
 * MapperScannerRegistry
 *
 * @author Otstar Lin
 * @date 2020/10/14 下午 1:54
 */
@Component(name = "mapperScannerRegistry")
public class MapperScannerRegistry implements AfterBeanRegistry {

    private final List<String> scanPackages = new ArrayList<>();

    @Override
    public void register(
        final XkJava app,
        final AnnotatedElement element,
        final MergedAnnotation annotation
    ) {
        if (annotation.hasAnnotation(MapperScan.class)) {
            this.processMapper(scanPackages, annotation);
        }
    }

    private void processMapper(
        final List<String> scanPackages,
        final MergedAnnotation annotation
    ) {
        for (final MapperScan scan : annotation.getAnnotations(
            MapperScan.class
        )) {
            scanPackages.addAll(Arrays.asList(scan.basePackages()));
            scanPackages.addAll(
                Arrays
                    .stream(scan.basePackageClasses())
                    .map(Class::getPackageName)
                    .collect(Collectors.toList())
            );
        }
    }

    public List<String> getScanPackages() {
        return scanPackages;
    }
}
