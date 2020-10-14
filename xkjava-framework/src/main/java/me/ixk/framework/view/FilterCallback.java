/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.view;

/**
 * 视图过滤器
 *
 * @author Otstar Lin
 * @date 2020/10/14 下午 5:15
 */
@FunctionalInterface
public interface FilterCallback {
    /**
     * 过滤
     *
     * @param result 结果
     *
     * @return 过滤后的结果
     */
    String filter(String result);
}
