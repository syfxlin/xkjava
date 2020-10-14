/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.utils;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 组合注解实现类
 *
 * @author Otstar Lin
 * @date 2020/10/14 下午 5:10
 */
public class MergedAnnotationImpl implements MergedAnnotation {
    Map<Class<? extends Annotation>, List<Annotation>> annotations;

    public MergedAnnotationImpl() {
        this.annotations = new LinkedHashMap<>();
    }

    public MergedAnnotationImpl(AnnotatedElement element) {
        this.annotations = AnnotationUtils.mergeAnnotation(element);
    }

    @Override
    public Map<Class<? extends Annotation>, List<Annotation>> annotations() {
        return this.annotations;
    }

    @Override
    public void addAnnotation(Annotation annotation) {
        List<Annotation> list =
            this.annotations.getOrDefault(
                    annotation.annotationType(),
                    new ArrayList<>()
                );
        list.add(AnnotationUtils.mergeAnnotationValue(annotation));
        this.annotations.put(annotation.annotationType(), list);
    }

    @Override
    public void removeAnnotation(Class<? extends Annotation> annotationType) {
        this.annotations.remove(annotationType);
    }

    @Override
    public void removeAnnotation(
        Class<? extends Annotation> annotationType,
        int index
    ) {
        List<Annotation> list = this.annotations.get(annotationType);
        if (list != null) {
            list.remove(index);
        }
    }
}
