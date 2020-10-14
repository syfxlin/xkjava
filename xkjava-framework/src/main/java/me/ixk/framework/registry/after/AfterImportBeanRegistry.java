/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.registry.after;

import java.lang.reflect.AnnotatedElement;
import me.ixk.framework.ioc.XkJava;
import me.ixk.framework.utils.MergedAnnotation;

/**
 * 后置 Import 注册记录
 *
 * @author Otstar Lin
 * @date 2020/10/14 下午 1:54
 */
public interface AfterImportBeanRegistry {
    /**
     * 注册
     *
     * @param app        应用
     * @param element    注解元素
     * @param annotation 组合注解
     */
    void register(
        XkJava app,
        AnnotatedElement element,
        MergedAnnotation annotation
    );
}
