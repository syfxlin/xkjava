/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.registry.request;

import java.lang.reflect.Method;
import me.ixk.framework.ioc.XkJava;
import me.ixk.framework.util.MergedAnnotation;

/**
 * 请求属性注册记录
 *
 * @author Otstar Lin
 * @date 2020/10/14 下午 2:05
 */
public interface RequestAttributeRegistry {
    /**
     * 注册
     *
     * @param app           应用
     * @param attributeName 属性名
     * @param method        方法
     * @param annotation    组合注解
     *
     * @return 属性值
     */
    Object register(
        XkJava app,
        String attributeName,
        Method method,
        MergedAnnotation annotation
    );
}
