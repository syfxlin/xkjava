/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.ioc;

import cn.hutool.core.collection.ConcurrentHashSet;
import cn.hutool.core.util.ReflectUtil;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import me.ixk.framework.annotation.ComponentScan;
import me.ixk.framework.annotation.Conditional;
import me.ixk.framework.util.AnnotationUtils;
import me.ixk.framework.util.MergedAnnotation;
import me.ixk.framework.util.ReflectionsUtils;
import me.ixk.framework.util.SoftCache;
import org.reflections.Reflections;

/**
 * Bean 扫描器
 *
 * @author Otstar Lin
 * @date 2020/11/29 下午 2:34
 */
public class BeanScanner {

    private static final SoftCache<Class<? extends Annotation>, Set<Class<?>>> CLASS_ANNOTATION_CACHE = new SoftCache<>();
    private static final SoftCache<Class<? extends Annotation>, Set<Method>> METHOD_ANNOTATION_CACHE = new SoftCache<>();
    private final XkJava app;
    private final Set<BeanScannerDefinition> scannerDefinitions = new ConcurrentHashSet<>();
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

    public Reflections getReflections() {
        if (this.reflections == null) {
            synchronized (this) {
                if (this.reflections != null) {
                    return this.reflections;
                }
                final Set<String> packages = new LinkedHashSet<>();
                Predicate<String> predicate = null;
                for (final BeanScannerDefinition definition : this.scannerDefinitions) {
                    packages.addAll(
                        Arrays.asList(definition.getScanPackages())
                    );
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
        }
        return this.reflections;
    }

    @SuppressWarnings("unchecked")
    public Set<Class<?>> getTypesAnnotated(
        final Class<? extends Annotation> annotation
    ) {
        return CLASS_ANNOTATION_CACHE.computeIfAbsent(
            annotation,
            a -> AnnotationUtils.sortByOrderAnnotation(getTypesAnnotatedWith(a))
        );
    }

    @SuppressWarnings("unchecked")
    public Set<Method> getMethodsAnnotated(
        final Class<? extends Annotation> annotation
    ) {
        return METHOD_ANNOTATION_CACHE.computeIfAbsent(
            annotation,
            a ->
                AnnotationUtils.sortByOrderAnnotation(
                    getMethodsAnnotatedWith(a)
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

    public boolean isCondition(final AnnotatedElement element) {
        final MergedAnnotation annotation = MergedAnnotation.from(element);
        final List<Conditional> conditionals = annotation.getAnnotations(
            Conditional.class
        );
        if (conditionals.isEmpty()) {
            return true;
        }
        for (Conditional conditional : conditionals) {
            for (Class<? extends Condition> condition : conditional.value()) {
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
        }
        return true;
    }
}
