/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.ioc.entity;

import java.lang.reflect.AnnotatedElement;
import me.ixk.framework.annotation.Skip;
import me.ixk.framework.util.MergedAnnotation;

/**
 * AnnotatedEntry
 *
 * @author Otstar Lin
 * @date 2020/11/9 下午 8:14
 */
public class AnnotatedEntry<E extends AnnotatedElement> {

    protected final E element;
    protected final MergedAnnotation annotation;

    public AnnotatedEntry(final E element) {
        this.element = element;
        this.annotation = MergedAnnotation.from(element);
    }

    public AnnotatedEntry(final E element, final MergedAnnotation annotation) {
        this.element = element;
        this.annotation = annotation;
    }

    public E getElement() {
        return element;
    }

    public MergedAnnotation getAnnotation() {
        return annotation;
    }

    public static class ChangeableEntry<E extends AnnotatedElement>
        extends AnnotatedEntry<E> {

        private boolean changed = false;

        public ChangeableEntry(final E element) {
            super(element);
        }

        public ChangeableEntry(
            final E element,
            final MergedAnnotation annotation
        ) {
            super(element, annotation);
        }

        public ChangeableEntry(final AnnotatedEntry<E> annotatedEntry) {
            super(annotatedEntry.getElement(), annotatedEntry.getAnnotation());
        }

        public boolean isChanged() {
            return annotation.hasAnnotation(Skip.class) || changed;
        }

        public void setChanged(final boolean changed) {
            this.changed = changed;
        }
    }
}
