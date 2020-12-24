/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.registry.attribute;

import java.lang.reflect.AnnotatedElement;
import me.ixk.framework.ioc.XkJava;
import me.ixk.framework.utils.MergedAnnotation;

/**
 * 属性注册记录
 *
 * @author Otstar Lin
 * @date 2020/10/14 下午 1:55
 */
public interface AttributeRegistry {
    /**
     * 注册
     *
     * @param app           应用
     * @param attributeName 属性名
     * @param element       注解元素
     * @param scopeType     作用域
     * @param annotation    组合注解
     *
     * @return 属性
     */
    Object register(
        XkJava app,
        String attributeName,
        AnnotatedElement element,
        String scopeType,
        MergedAnnotation annotation
    );
}
