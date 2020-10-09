/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.utils;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Map;

public interface MergeAnnotation {
    Map<Class<? extends Annotation>, Annotation> annotations();

    List<Class<? extends Annotation>> indexes();

    default <A extends Annotation> A getTarget() {
        return this.getAnnotation(0);
    }

    default <A extends Annotation> A getRoot() {
        return this.getAnnotation(this.indexes().size() - 1);
    }

    default <T> T get(final String key) {
        return AnnotationUtils.getAnnotationValue(this, key);
    }

    default <T> T get(final String key, final Class<T> returnType) {
        return AnnotationUtils.getAnnotationValue(this, returnType, key);
    }

    default <T> T get(
        final Class<? extends Annotation> annotationType,
        final String key
    ) {
        return AnnotationUtils.getAnnotationValue(this, key, annotationType);
    }

    @SuppressWarnings("unchecked")
    default <A extends Annotation> A getAnnotation(Class<A> annotationType) {
        return (A) this.annotations().get(annotationType);
    }

    @SuppressWarnings("unchecked")
    default <A extends Annotation> A getAnnotation(int index) {
        List<Class<? extends Annotation>> indexes = this.indexes();
        if (index < 0 || index >= indexes.size()) {
            return null;
        }
        return (A) this.annotations().get(indexes.get(index));
    }

    default int size() {
        return this.indexes().size();
    }
}
