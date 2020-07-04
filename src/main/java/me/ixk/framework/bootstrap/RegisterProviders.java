/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.bootstrap;

import me.ixk.framework.annotations.Bootstrap;
import me.ixk.framework.annotations.Order;
import me.ixk.framework.annotations.processor.ProviderAnnotationProcessor;
import me.ixk.framework.ioc.Application;

@Bootstrap
@Order(Order.HIGHEST_PRECEDENCE + 5)
public class RegisterProviders extends AbstractBootstrap {

    public RegisterProviders(Application app) {
        super(app);
    }

    @Override
    public void boot() {
        ProviderAnnotationProcessor processor = new ProviderAnnotationProcessor(
            this.app
        );
        processor.process();
    }
}
