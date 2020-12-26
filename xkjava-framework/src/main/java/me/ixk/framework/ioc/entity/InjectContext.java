/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.ioc.entity;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import me.ixk.framework.ioc.binder.DataBinder;
import me.ixk.framework.ioc.entity.AnnotatedEntry.ChangeableEntry;
import me.ixk.framework.utils.MergedAnnotation;

/**
 * 注入上下文
 *
 * @author Otstar Lin
 * @date 2020/12/26 下午 8:52
 */
public class InjectContext {

    private final Binding binding;
    private final DataBinder binder;
    private final Map<String, Object> injectData = new ConcurrentHashMap<>();

    private final ChangeableEntry<Method>[] methodEntries;
    private final ChangeableEntry<Field>[] fieldEntries;

    @SuppressWarnings("unchecked")
    public InjectContext(Binding binding, DataBinder binder) {
        this.binding = binding;
        this.binder = binder;

        final AnnotatedEntry<Field>[] fieldEntries =
            this.binding.getFieldEntries();
        this.fieldEntries = new ChangeableEntry[fieldEntries.length];
        for (int i = 0; i < fieldEntries.length; i++) {
            this.fieldEntries[i] = new ChangeableEntry<>(fieldEntries[i]);
        }
        final AnnotatedEntry<Method>[] methodEntries =
            this.binding.getMethodEntries();
        this.methodEntries = new ChangeableEntry[methodEntries.length];
        for (int i = 0; i < methodEntries.length; i++) {
            this.methodEntries[i] = new ChangeableEntry<>(methodEntries[i]);
        }
    }

    public Binding getBinding() {
        return binding;
    }

    public DataBinder getBinder() {
        return binder;
    }

    public Map<String, Object> getInjectData() {
        return injectData;
    }

    public void putData(String key, Object value) {
        injectData.put(key, value);
    }

    @SuppressWarnings("unchecked")
    public <T> T getData(String key) {
        return (T) injectData.get(key);
    }

    public AnnotatedEntry<Class<?>> getInstanceEntry() {
        return binding.getInstanceTypeEntry();
    }

    public Class<?> getType() {
        return binding.getType();
    }

    public MergedAnnotation getAnnotation() {
        return binding.getAnnotation();
    }

    public ChangeableEntry<Method>[] getMethodEntries() {
        return methodEntries;
    }

    public ChangeableEntry<Field>[] getFieldEntries() {
        return fieldEntries;
    }
}
