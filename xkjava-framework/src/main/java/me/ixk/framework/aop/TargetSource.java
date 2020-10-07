/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.aop;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class TargetSource {
    private final Class<?> targetType;
    private final Class<?>[] interfaces;
    private final Object target;
    private final AspectManager aspectManager;

    public TargetSource(
        AspectManager aspectManager,
        Object target,
        Class<?> targetType,
        Class<?>[] interfaces
    ) {
        this.aspectManager = aspectManager;
        this.targetType = targetType;
        this.interfaces = interfaces;
        this.target = target;
    }

    public Class<?> getTargetType() {
        return targetType;
    }

    public Class<?>[] getInterfaces() {
        return interfaces;
    }

    public Object getTarget() {
        return target;
    }

    public List<Advice> getAdvices(Method method) {
        if (this.aspectManager == null) {
            return new ArrayList<>();
        }
        return this.aspectManager.getAdvices(method);
    }
}
