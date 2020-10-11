/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.utils;

import cn.hutool.core.util.ReflectUtil;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public interface MergedAnnotation {
    Map<Class<? extends Annotation>, List<Annotation>> annotations();

    default <A extends Annotation> A getAnnotation(
        final Class<A> annotationType
    ) {
        return this.getAnnotation(annotationType, 0);
    }

    @SuppressWarnings("unchecked")
    default <A extends Annotation> A getAnnotation(
        final Class<A> annotationType,
        final int index
    ) {
        final List<Annotation> annotations =
            this.annotations().get(annotationType);
        if (
            annotations == null ||
            annotations.isEmpty() ||
            index < 0 ||
            index >= annotations.size()
        ) {
            return null;
        }
        return (A) annotations.get(index);
    }

    @SuppressWarnings("unchecked")
    default <A extends Annotation> List<A> getAnnotations(
        Class<A> annotationType
    ) {
        return (List<A>) this.annotations()
            .getOrDefault(annotationType, new ArrayList<>());
    }

    default boolean hasAnnotation(
        final Class<? extends Annotation> annotationType
    ) {
        return this.annotations().containsKey(annotationType);
    }

    default boolean notAnnotation(Class<? extends Annotation> annotationType) {
        return !this.hasAnnotation(annotationType);
    }

    default boolean hasMultiAnnotation(
        final Class<? extends Annotation> annotationType
    ) {
        return (
            this.annotations().containsKey(annotationType) &&
            this.annotations().get(annotationType).size() != 1
        );
    }

    default void addAnnotation(Annotation annotation) {
        throw new UnsupportedOperationException(
            "Unsupported add annotation to merge annotation"
        );
    }

    default void removeAnnotation(Class<? extends Annotation> annotationType) {
        throw new UnsupportedOperationException(
            "Unsupported remove annotation to merge annotation"
        );
    }

    default void removeAnnotation(
        Class<? extends Annotation> annotationType,
        int index
    ) {
        throw new UnsupportedOperationException(
            "Unsupported add annotation to merge annotation"
        );
    }

    default <T> T get(Class<? extends Annotation> annotationType, String name) {
        return this.get(annotationType, name, 0);
    }

    default <T> T get(
        Class<? extends Annotation> annotationType,
        String name,
        int index
    ) {
        final Annotation annotation = this.getAnnotation(annotationType, index);
        final Method method = ReflectUtil.getMethodByName(annotationType, name);
        if (annotation == null || method == null) {
            return null;
        }
        return ReflectUtil.invoke(annotation, method);
    }
}
