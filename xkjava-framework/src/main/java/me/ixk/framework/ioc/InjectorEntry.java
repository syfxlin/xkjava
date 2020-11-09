/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.ioc;

import java.lang.reflect.AnnotatedElement;
import me.ixk.framework.utils.AnnotationUtils;
import me.ixk.framework.utils.MergedAnnotation;

/**
 * InjectorEntry
 *
 * @author Otstar Lin
 * @date 2020/11/9 下午 8:14
 */
public class InjectorEntry<E extends AnnotatedElement> {
    private final E element;
    private final MergedAnnotation annotation;
    private volatile boolean changed = false;

    public InjectorEntry(E element) {
        this.element = element;
        this.annotation = AnnotationUtils.getAnnotation(element);
    }

    public E getElement() {
        return element;
    }

    public MergedAnnotation getAnnotation() {
        return annotation;
    }

    public boolean isChanged() {
        return changed;
    }

    public void setChanged(boolean changed) {
        this.changed = changed;
    }
}
