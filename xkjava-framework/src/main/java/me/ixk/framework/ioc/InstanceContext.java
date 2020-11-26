/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.ioc;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
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

    public InstanceContext(Binding binding, Class<?> instanceType) {
        this.binding = binding;
        this.instanceEntry = new InjectorEntry<>(instanceType);
    }

    public Binding getBinding() {
        return binding;
    }

    public InjectorEntry<Class<?>> getInstanceEntry() {
        return instanceEntry;
    }

    public InjectorEntry<Field>[] getFieldEntries() {
        return this.binding.getFieldEntries();
    }

    public InjectorEntry<Method>[] getMethodEntries() {
        return this.binding.getMethodEntries();
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

    public Method[] getMethods() {
        return Arrays
            .stream(this.getMethodEntries())
            .map(InjectorEntry::getElement)
            .toArray(Method[]::new);
    }

    public MergedAnnotation[] getMethodAnnotations() {
        return Arrays
            .stream(this.getMethodEntries())
            .map(InjectorEntry::getAnnotation)
            .toArray(MergedAnnotation[]::new);
    }
}
