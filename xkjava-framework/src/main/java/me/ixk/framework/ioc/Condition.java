/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.ioc;

import java.lang.reflect.AnnotatedElement;
import me.ixk.framework.utils.MergedAnnotation;

/**
 * 条件接口
 *
 * @author Otstar Lin
 * @date 2020/10/14 上午 11:16
 */
@FunctionalInterface
public interface Condition {
    /**
     * 匹配
     *
     * @param app        应用
     * @param element    注解元素
     * @param annotation 组合注解
     *
     * @return 是否匹配
     */
    boolean matches(
        XkJava app,
        AnnotatedElement element,
        MergedAnnotation annotation
    );
}
