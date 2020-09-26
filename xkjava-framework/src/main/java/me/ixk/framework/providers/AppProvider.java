/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.providers;

import me.ixk.framework.annotations.Order;
import me.ixk.framework.annotations.Provider;
import me.ixk.framework.ioc.XkJava;

@Provider
@Order(Order.HIGHEST_PRECEDENCE + 9)
public class AppProvider extends AbstractProvider {

    public AppProvider(XkJava app) {
        super(app);
    }

    @Override
    public void register() {
        //
        System.out.println("register");
    }

    @Override
    public void boot() {
        //
        System.out.println("boot");
    }
}