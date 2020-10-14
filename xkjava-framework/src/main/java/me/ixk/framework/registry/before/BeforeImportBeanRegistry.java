/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.registry.before;

import java.lang.reflect.AnnotatedElement;
import me.ixk.framework.ioc.XkJava;
import me.ixk.framework.utils.MergedAnnotation;

/**
 * 前置 Import 注册记录
 *
 * @author Otstar Lin
 * @date 2020/10/14 下午 1:58
 */
public interface BeforeImportBeanRegistry {
    /**
     * 注册
     *
     * @param app        应用
     * @param element    注解元素
     * @param annotation 组合注解
     */
    void before(
        XkJava app,
        AnnotatedElement element,
        MergedAnnotation annotation
    );
}
