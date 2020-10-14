/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.registry;

import me.ixk.framework.annotations.ScopeType;
import me.ixk.framework.ioc.Binding;
import me.ixk.framework.ioc.XkJava;
import me.ixk.framework.utils.MergedAnnotation;

/**
 * Import 注册记录
 *
 * @author Otstar Lin
 * @date 2020/10/14 下午 2:06
 */
public interface ImportBeanRegistry {
    /**
     * 注册
     *
     * @param app        应用
     * @param clazz      类型
     * @param scopeType  作用域
     * @param annotation 组合注解
     *
     * @return Binding
     */
    Binding register(
        XkJava app,
        Class<?> clazz,
        ScopeType scopeType,
        MergedAnnotation annotation
    );
}
