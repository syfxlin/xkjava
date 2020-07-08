/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.utils;

import cn.hutool.core.lang.SimpleCache;
import me.ixk.framework.ioc.XkJava;
import org.reflections.Reflections;
import org.reflections.scanners.*;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public abstract class ReflectionsUtils {
    private static final Reflections GLOBAL_APP_REFLECTIONS = make(
        XkJava.of().scanPackage().toArray(new String[0])
    );

    private static final SimpleCache<Class<? extends Annotation>, List<Class<?>>> CLASS_ANNOTATION_CACHE = new SimpleCache<>();
    private static final SimpleCache<Class<? extends Annotation>, List<Method>> METHOD_ANNOTATION_CACHE = new SimpleCache<>();

    public static Reflections make() {
        return GLOBAL_APP_REFLECTIONS;
    }

    public static Reflections make(Class<?> _class) {
        return make(ClasspathHelper.forClass(_class));
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

    @SuppressWarnings("unchecked")
    public static List<Class<?>> getTypesAnnotatedWith(
        Class<? extends Annotation> annotation
    ) {
        List<Class<?>> cache = CLASS_ANNOTATION_CACHE.get(annotation);
        if (cache != null) {
            return cache;
        }
        return CLASS_ANNOTATION_CACHE.put(
            annotation,
            (List<Class<?>>) AnnotationUtils.sortByOrderAnnotation(
                make().getTypesAnnotatedWith(annotation)
            )
        );
    }

    @SuppressWarnings("unchecked")
    public static List<Method> getMethodsAnnotatedWith(
        Class<? extends Annotation> annotation
    ) {
        List<Method> cache = METHOD_ANNOTATION_CACHE.get(annotation);
        if (cache != null) {
            return cache;
        }
        return METHOD_ANNOTATION_CACHE.put(
            annotation,
            (List<Method>) AnnotationUtils.sortByOrderAnnotation(
                make().getMethodsAnnotatedWith(annotation)
            )
        );
    }
}
