/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.registry;

import java.lang.reflect.AnnotatedElement;
import me.ixk.framework.ioc.XkJava;
import me.ixk.framework.ioc.entity.Binding;
import me.ixk.framework.util.MergedAnnotation;

/**
 * BindRegistry 注册记录
 *
 * @author Otstar Lin
 * @date 2020/10/14 下午 2:06
 */
public interface BeanBindRegistry {
    /**
     * 注册
     *
     * @param app        应用
     * @param element    类型
     * @param scopeType  作用域
     * @param annotation 组合注解
     *
     * @return Binding
     */
    Binding register(
        XkJava app,
        AnnotatedElement element,
        String scopeType,
        MergedAnnotation annotation
    );
}
