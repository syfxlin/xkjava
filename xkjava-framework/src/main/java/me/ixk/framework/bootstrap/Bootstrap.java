/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.bootstrap;

/**
 * 启动（接口）
 * <p>
 * 在 App 启动的时候会调用所有的启动类来对系统进行加载
 *
 * @author Otstar Lin
 * @date 2020/10/14 上午 8:49
 */
public interface Bootstrap {
    /**
     * 启动方法
     */
    void boot();
}
