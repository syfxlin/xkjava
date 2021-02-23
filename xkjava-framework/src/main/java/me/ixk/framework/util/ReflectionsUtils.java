/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.util;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;
import org.reflections.Reflections;
import org.reflections.scanners.FieldAnnotationsScanner;
import org.reflections.scanners.MethodAnnotationsScanner;
import org.reflections.scanners.Scanner;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

/**
 * 注解扫描工具类
 *
 * @author Otstar Lin
 * @date 2020/10/14 下午 5:14
 */
public class ReflectionsUtils {

    public static Reflections make(final Class<?> clazz) {
        return make(ClasspathHelper.forClass(clazz));
    }

    public static Reflections make(final String packageName) {
        return make(ClasspathHelper.forPackage(packageName));
    }

    public static Reflections make(
        final String prefix,
        final Scanner... scanners
    ) {
        return new Reflections(prefix, scanners);
    }

    public static Reflections make(
        final String[] packages,
        final Predicate<String> filter
    ) {
        final List<URL> urls = new ArrayList<>(packages.length);
        for (final String item : packages) {
            urls.addAll(ClasspathHelper.forPackage(item));
        }
        return make(urls, filter);
    }

    public static Reflections make(
        final Collection<URL> urls,
        final Predicate<String> filter
    ) {
        final ConfigurationBuilder builder = new ConfigurationBuilder();
        builder.addUrls(urls);
        builder.addScanners(
            new TypeAnnotationsScanner(),
            new MethodAnnotationsScanner(),
            new FieldAnnotationsScanner(),
            new SubTypesScanner()
        );
        builder.filterInputsBy(filter);
        return new Reflections(builder);
    }

    public static Reflections make(final Collection<URL> urls) {
        return new Reflections(
            new ConfigurationBuilder()
                .setUrls(urls)
                .setScanners(
                    new TypeAnnotationsScanner(),
                    new MethodAnnotationsScanner(),
                    new FieldAnnotationsScanner(),
                    new SubTypesScanner()
                )
        );
    }

    public static Reflections make(final String... packages) {
        final List<URL> urls = new ArrayList<>(packages.length);
        for (final String item : packages) {
            urls.addAll(ClasspathHelper.forPackage(item));
        }
        return make(urls);
    }

    public static Reflections make(final URL... urls) {
        return make(Arrays.asList(urls));
    }
}
