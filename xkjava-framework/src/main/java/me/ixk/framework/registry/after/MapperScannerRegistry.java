/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.registry.after;

import java.lang.reflect.AnnotatedElement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import me.ixk.framework.annotations.Component;
import me.ixk.framework.annotations.MapperScan;
import me.ixk.framework.ioc.XkJava;
import me.ixk.framework.utils.MergedAnnotation;

@Component(name = "mapperScannerRegistry")
public class MapperScannerRegistry implements AfterImportBeanRegistry {

    private final List<String> scanPackages = new ArrayList<>();

    @Override
    public void after(final XkJava app, final AnnotatedElement element,
        final MergedAnnotation annotation) {
        if (annotation.hasAnnotation(MapperScan.class)) {
            this.processMapper(scanPackages, annotation);
        }
    }

    private void processMapper(final List<String> scanPackages,
        final MergedAnnotation annotation) {
        for (final MapperScan scan : annotation.getAnnotations(MapperScan.class)) {
            scanPackages.addAll(Arrays.asList(scan.basePackages()));
            scanPackages.addAll(Arrays.stream(scan.basePackageClasses())
                                      .map(Class::getPackageName)
                                      .collect(Collectors.toList()));
        }
    }

    public List<String> getScanPackages() {
        return scanPackages;
    }
}
