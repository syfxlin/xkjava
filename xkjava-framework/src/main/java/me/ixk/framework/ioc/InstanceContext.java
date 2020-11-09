/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.ioc;

import java.lang.reflect.Field;
import java.util.Arrays;
import me.ixk.framework.utils.MergedAnnotation;

/**
 * 对象上下文
 *
 * @author Otstar Lin
 * @date 2020/11/9 下午 8:01
 */
public class InstanceContext {
    private final Binding binding;
    private final InjectorEntry<Class<?>> instanceEntry;
    private final InjectorEntry<Field>[] fieldEntries;

    @SuppressWarnings("unchecked")
    public InstanceContext(Binding binding, Class<?> instanceType) {
        this.binding = binding;
        this.instanceEntry = new InjectorEntry<>(instanceType);
        final Field[] fields = instanceType.getDeclaredFields();
        this.fieldEntries =
            Arrays
                .stream(fields)
                .map(InjectorEntry::new)
                .toArray(InjectorEntry[]::new);
    }

    public Binding getBinding() {
        return binding;
    }

    public InjectorEntry<Class<?>> getInstanceEntry() {
        return instanceEntry;
    }

    public InjectorEntry<Field>[] getFieldEntries() {
        return fieldEntries;
    }

    public Class<?> getInstanceType() {
        return this.getInstanceEntry().getElement();
    }

    public MergedAnnotation getAnnotation() {
        return this.getInstanceEntry().getAnnotation();
    }

    public Field[] getFields() {
        return Arrays
            .stream(this.getFieldEntries())
            .map(InjectorEntry::getElement)
            .toArray(Field[]::new);
    }

    public MergedAnnotation[] getFieldAnnotations() {
        return Arrays
            .stream(this.getFieldEntries())
            .map(InjectorEntry::getAnnotation)
            .toArray(MergedAnnotation[]::new);
    }
}
