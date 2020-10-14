/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.bootstrap;

import me.ixk.framework.annotations.Bootstrap;
import me.ixk.framework.annotations.Order;
import me.ixk.framework.aop.AspectManager;
import me.ixk.framework.ioc.XkJava;

/**
 * 早期注入启动类
 * <p>
 * 由于一些对象需要被早期使用，所以需要在 Bean 绑定之前注入到容器中
 *
 * @author Otstar Lin
 * @date 2020/10/14 上午 8:50
 */
@Bootstrap
@Order(Order.HIGHEST_PRECEDENCE + 2)
public class EarlyInjection extends AbstractBootstrap {

    public EarlyInjection(XkJava app) {
        super(app);
    }

    @Override
    public void boot() {
        // 切面管理器
        this.app.singleton(
                AspectManager.class,
                AspectManager.class,
                "aspectManager"
            );
        this.app.make(AspectManager.class);
    }
}
