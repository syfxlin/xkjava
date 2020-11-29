/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.ioc;

import cn.hutool.core.lang.SimpleCache;
import cn.hutool.core.util.ReflectUtil;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import me.ixk.framework.annotations.ComponentScan;
import me.ixk.framework.annotations.Conditional;
import me.ixk.framework.utils.AnnotationUtils;
import me.ixk.framework.utils.MergedAnnotation;
import me.ixk.framework.utils.ReflectionsUtils;
import org.reflections.Reflections;

/**
 * Bean 扫描器
 *
 * @author Otstar Lin
 * @date 2020/11/29 下午 2:34
 */
public class BeanScanner {

    private static final SimpleCache<Class<? extends Annotation>, Set<Class<?>>> CLASS_ANNOTATION_CACHE = new SimpleCache<>();
    private static final SimpleCache<Class<? extends Annotation>, Set<Method>> METHOD_ANNOTATION_CACHE = new SimpleCache<>();
    private final XkJava app;
    private final Set<BeanScannerDefinition> scannerDefinitions = new LinkedHashSet<>();
    private Reflections reflections;

    public BeanScanner(final XkJava app) {
        this.app = app;
    }

    public XkJava getApp() {
        return app;
    }

    public void addDefinition(final ComponentScan componentScan) {
        this.scannerDefinitions.add(
                new BeanScannerDefinition(this, componentScan)
            );
    }

    public void addDefinition(final String[] scanPackages) {
        this.scannerDefinitions.add(
                new BeanScannerDefinition(this, scanPackages)
            );
    }

    private Reflections getReflections() {
        if (this.reflections == null) {
            final Set<String> packages = new LinkedHashSet<>();
            Predicate<String> predicate = null;
            for (final BeanScannerDefinition definition : this.scannerDefinitions) {
                packages.addAll(Arrays.asList(definition.getScanPackages()));
                predicate =
                    predicate == null
                        ? definition.getFilter()
                        : predicate.and(definition.getFilter());
            }
            this.reflections =
                ReflectionsUtils.make(
                    packages.toArray(String[]::new),
                    predicate
                );
        }
        return this.reflections;
    }

    @SuppressWarnings("unchecked")
    public Set<Class<?>> getTypesAnnotated(
        final Class<? extends Annotation> annotation
    ) {
        final Set<Class<?>> cache = CLASS_ANNOTATION_CACHE.get(annotation);
        if (cache != null) {
            return cache;
        }
        return CLASS_ANNOTATION_CACHE.put(
            annotation,
            AnnotationUtils.sortByOrderAnnotation(
                getTypesAnnotatedWith(annotation)
            )
        );
    }

    @SuppressWarnings("unchecked")
    public Set<Method> getMethodsAnnotated(
        final Class<? extends Annotation> annotation
    ) {
        final Set<Method> cache = METHOD_ANNOTATION_CACHE.get(annotation);
        if (cache != null) {
            return cache;
        }
        return METHOD_ANNOTATION_CACHE.put(
            annotation,
            AnnotationUtils.sortByOrderAnnotation(
                getMethodsAnnotatedWith(annotation)
            )
        );
    }

    @SuppressWarnings("unchecked")
    private Set<Class<?>> getTypesAnnotatedWith(
        final Class<? extends Annotation> annotation
    ) {
        final Set<Class<?>> set = new LinkedHashSet<>();
        final Reflections reflection = this.getReflections();
        for (final Class<?> item : reflection.getTypesAnnotatedWith(
            annotation
        )) {
            if (item.isAnnotation()) {
                set.addAll(
                    getTypesAnnotatedWith((Class<? extends Annotation>) item)
                );
            } else {
                set.add(item);
            }
        }
        final Class<? extends Annotation> repeatable = AnnotationUtils.getRepeatable(
            annotation
        );
        if (repeatable != null) {
            set.addAll(getTypesAnnotatedWith(repeatable));
        }
        return set;
    }

    @SuppressWarnings("unchecked")
    private Set<Method> getMethodsAnnotatedWith(
        final Class<? extends Annotation> annotation
    ) {
        final Reflections reflection = this.getReflections();
        final Set<Method> set = new LinkedHashSet<>(
            reflection.getMethodsAnnotatedWith(annotation)
        );
        for (final Class<?> item : reflection.getTypesAnnotatedWith(
            annotation
        )) {
            if (item.isAnnotation()) {
                set.addAll(
                    getMethodsAnnotatedWith((Class<? extends Annotation>) item)
                );
            }
        }
        return set;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public Set filterConditionAnnotation(final Collection classes) {
        return (Set) classes
            .stream()
            .filter(clazz -> isCondition((AnnotatedElement) clazz))
            .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    @SuppressWarnings("unchecked")
    public boolean isCondition(final AnnotatedElement element) {
        final MergedAnnotation annotation = AnnotationUtils.getAnnotation(
            element
        );
        if (annotation.notAnnotation(Conditional.class)) {
            return true;
        }
        for (final Class<? extends Condition> condition : (Class<? extends Condition>[]) annotation.get(
            Conditional.class,
            "value"
        )) {
            final boolean matches = ReflectUtil.invoke(
                ReflectUtil.newInstance(condition),
                "matches",
                XkJava.of(),
                element,
                annotation
            );
            if (!matches) {
                return false;
            }
        }
        return true;
    }
}
