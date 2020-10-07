/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.bootstrap;

import me.ixk.framework.annotations.Bootstrap;
import me.ixk.framework.annotations.Order;
import me.ixk.framework.aop.AspectManager;
import me.ixk.framework.ioc.XkJava;

@Bootstrap
@Order(Order.HIGHEST_PRECEDENCE + 2)
public class EarlyInjection extends AbstractBootstrap {

    public EarlyInjection(XkJava app) {
        super(app);
    }

    @Override
    public void boot() {
        this.app.singleton(
                AspectManager.class,
                AspectManager.class,
                "aspectManager"
            );
        this.app.make(AspectManager.class);
    }
}
