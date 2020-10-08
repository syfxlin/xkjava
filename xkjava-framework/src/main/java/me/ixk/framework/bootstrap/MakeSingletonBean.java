/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.bootstrap;

import java.util.List;
import me.ixk.framework.annotations.Bootstrap;
import me.ixk.framework.annotations.Order;
import me.ixk.framework.ioc.XkJava;

@Bootstrap
@Order(Order.HIGHEST_PRECEDENCE + 6)
public class MakeSingletonBean extends AbstractBootstrap {

    public MakeSingletonBean(final XkJava app) {
        super(app);
    }

    @Override
    public void boot() {
        for (Object beanName : this.app.getAttribute(
                "makeSingletonBeanList",
                List.class
            )) {
            this.app.make((String) beanName);
        }
    }
}
