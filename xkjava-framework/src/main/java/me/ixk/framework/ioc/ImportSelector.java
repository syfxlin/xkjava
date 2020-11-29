/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.ioc;

/**
 * 导入选择器
 *
 * @author Otstar Lin
 * @date 2020/11/29 下午 1:42
 */
public interface ImportSelector {
    /**
     * 选择导入的组件
     *
     * @param classAnnotatedEntry 类和注解的信息
     *
     * @return 要导入的全类名
     */
    String[] selectImports(AnnotatedEntry<Class<?>> classAnnotatedEntry);
}
