/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.kernel;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import me.ixk.framework.annotations.InitBinder;
import me.ixk.framework.utils.AnnotationUtils;

public class InitBinderHandlerResolver {
    private final List<Method> methodList = new ArrayList<>();

    public InitBinderHandlerResolver(Class<?> _class) {
        for (Method method : _class.getDeclaredMethods()) {
            InitBinder initBinder = AnnotationUtils.getAnnotation(
                method,
                InitBinder.class
            );
            if (initBinder != null) {
                this.methodList.add(method);
            }
        }
    }

    public boolean hasInitBinderList() {
        return !this.methodList.isEmpty();
    }

    public List<Method> resolveMethods() {
        return this.methodList;
    }
}