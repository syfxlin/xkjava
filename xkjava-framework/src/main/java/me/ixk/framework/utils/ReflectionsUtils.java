/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.utils;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import me.ixk.framework.ioc.XkJava;
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
    private static final Reflections GLOBAL_APP_REFLECTIONS = make(
        XkJava.of().scanPackage().toArray(new String[0])
    );

    public static Reflections make() {
        return GLOBAL_APP_REFLECTIONS;
    }

    public static Reflections make(Class<?> clazz) {
        return make(ClasspathHelper.forClass(clazz));
    }

    public static Reflections make(String _package) {
        return make(ClasspathHelper.forPackage(_package));
    }

    public static Reflections make(String prefix, Scanner... scanners) {
        return new Reflections(prefix, scanners);
    }

    public static Reflections make(Collection<URL> urls) {
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

    public static Reflections make(String... packages) {
        List<URL> urls = new ArrayList<>(packages.length);
        for (String item : packages) {
            urls.addAll(ClasspathHelper.forPackage(item));
        }
        return make(urls);
    }

    public static Reflections make(URL... urls) {
        return make(Arrays.asList(urls));
    }
}
