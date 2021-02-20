/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.ioc;

import cn.hutool.core.exceptions.UtilException;
import cn.hutool.core.util.ClassUtil;
import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import me.ixk.framework.annotation.ComponentScan;
import me.ixk.framework.annotation.FilterType;
import me.ixk.framework.ioc.entity.AnnotatedEntry;
import me.ixk.framework.util.MergedAnnotation;

/**
 * Bean 扫描定义
 *
 * @author Otstar Lin
 * @date 2020/11/29 下午 2:36
 */
public class BeanScannerDefinition {

    private final BeanScanner scanner;
    private final String[] scanPackages;
    private final Filter[] includeFilters;
    private final Filter[] excludeFilters;

    public BeanScannerDefinition(
        final BeanScanner scanner,
        final ComponentScan componentScan
    ) {
        this.scanner = scanner;
        this.scanPackages = componentScan.basePackages();
        final ComponentScan.Filter[] includeFilters = componentScan.includeFilters();
        this.includeFilters = new Filter[includeFilters.length];
        for (int i = 0; i < includeFilters.length; i++) {
            this.includeFilters[i] = new Filter(includeFilters[i]);
        }
        final ComponentScan.Filter[] excludeFilters = componentScan.excludeFilters();
        this.excludeFilters = new Filter[excludeFilters.length];
        for (int i = 0; i < excludeFilters.length; i++) {
            this.excludeFilters[i] = new Filter(excludeFilters[i]);
        }
    }

    public BeanScannerDefinition(
        final BeanScanner scanner,
        final String[] scanPackages
    ) {
        this.scanner = scanner;
        this.scanPackages = scanPackages;
        this.includeFilters = new Filter[0];
        this.excludeFilters = new Filter[0];
    }

    public String[] getScanPackages() {
        return scanPackages;
    }

    public Filter[] getIncludeFilters() {
        return includeFilters;
    }

    public Filter[] getExcludeFilters() {
        return excludeFilters;
    }

    public Predicate<String> getFilter() {
        return name -> {
            if (!name.endsWith(".class")) {
                return false;
            }
            final Class<Object> clazz;
            try {
                clazz =
                    ClassUtil.loadClass(
                        name.replace("/", ".").replace(".class", "")
                    );
            } catch (final UtilException e) {
                return false;
            }
            boolean included = false;
            for (final Filter filter : this.includeFilters) {
                if (filter.getFilter().test(clazz)) {
                    included = true;
                    break;
                }
            }
            if (!included && this.includeFilters.length > 0) {
                return false;
            }
            for (final Filter filter : this.excludeFilters) {
                if (filter.getFilter().test(clazz)) {
                    return false;
                }
            }
            return true;
        };
    }

    @Override
    public int hashCode() {
        int result = Arrays.hashCode(scanPackages);
        result = 31 * result + Arrays.hashCode(includeFilters);
        result = 31 * result + Arrays.hashCode(excludeFilters);
        return result;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final BeanScannerDefinition that = (BeanScannerDefinition) o;
        return (
            Arrays.equals(scanPackages, that.scanPackages) &&
            Arrays.equals(includeFilters, that.includeFilters) &&
            Arrays.equals(excludeFilters, that.excludeFilters)
        );
    }

    public class Filter {

        private final FilterType type;
        private final Class<?>[] classes;
        private final String[] pattern;

        public Filter(final ComponentScan.Filter filter) {
            this.type = filter.type();
            this.classes = filter.classes();
            this.pattern = filter.pattern();
        }

        public Filter(
            final FilterType type,
            final Class<?>[] classes,
            final String[] pattern
        ) {
            this.type = type;
            this.classes = classes;
            this.pattern = pattern;
        }

        public FilterType getType() {
            return type;
        }

        public Class<?>[] getClasses() {
            return classes;
        }

        public String[] getPattern() {
            return pattern;
        }

        @SuppressWarnings("unchecked")
        public Predicate<Class<?>> getFilter() {
            return type -> {
                switch (this.type) {
                    case ANNOTATION:
                    case ASSIGNABLE_TYPE:
                        for (final Class<?> clazz : this.classes) {
                            if (this.type == FilterType.ANNOTATION) {
                                if (
                                    type == clazz ||
                                    MergedAnnotation.has(
                                        type,
                                        (Class<? extends Annotation>) clazz
                                    )
                                ) {
                                    return true;
                                }
                            } else if (clazz.isAssignableFrom(type)) {
                                return true;
                            }
                        }
                        return false;
                    case REGEX:
                        for (final String pattern : this.pattern) {
                            if (Pattern.matches(pattern, type.getName())) {
                                return true;
                            }
                        }
                        return false;
                    case CUSTOM:
                        for (final Class<?> clazz : this.classes) {
                            if (ScanFilter.class.isAssignableFrom(clazz)) {
                                if (
                                    scanner
                                        .getApp()
                                        .make((Class<ScanFilter>) clazz)
                                        .match(new AnnotatedEntry<>(type))
                                ) {
                                    return true;
                                }
                            }
                        }
                        return false;
                    default:
                    //
                }
                return false;
            };
        }
    }
}
