/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.ioc;

/**
 * 扫描过滤器
 *
 * @author Otstar Lin
 * @date 2020/11/29 下午 3:20
 */
public interface ScanFilter {
    /**
     * 匹配
     *
     * @param annotatedEntry 类和注解信息
     *
     * @return 是否匹配
     */
    boolean match(AnnotatedEntry<Class<?>> annotatedEntry);
}
