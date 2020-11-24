/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.web.resolver;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import me.ixk.framework.annotations.InitBinder;
import me.ixk.framework.utils.AnnotationUtils;

/**
 * 初始化绑定处理程序解析器
 *
 * @author Otstar Lin
 * @date 2020/10/14 下午 1:33
 */
public class InitBinderHandlerResolver {
    private final List<Method> methodList = new ArrayList<>();

    public InitBinderHandlerResolver(Class<?> clazz) {
        for (Method method : clazz.getDeclaredMethods()) {
            if (AnnotationUtils.hasAnnotation(method, InitBinder.class)) {
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
