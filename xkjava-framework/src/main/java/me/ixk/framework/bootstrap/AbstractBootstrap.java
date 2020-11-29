/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.bootstrap;

import me.ixk.framework.ioc.BeanScanner;
import me.ixk.framework.ioc.XkJava;

/**
 * 启动（抽象类）
 *
 * @author Otstar Lin
 * @date 2020/10/14 上午 8:46
 */
public abstract class AbstractBootstrap implements Bootstrap {

    protected final XkJava app;
    protected final BeanScanner scanner;

    public AbstractBootstrap(final XkJava app) {
        this.app = app;
        this.scanner = app.beanScanner();
    }
}
