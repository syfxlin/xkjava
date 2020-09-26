/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.providers;

import me.ixk.framework.annotations.Order;
import me.ixk.framework.annotations.Provider;
import me.ixk.framework.ioc.XkJava;
import me.ixk.framework.utils.Hash;

@Provider
@Order(Order.HIGHEST_PRECEDENCE + 7)
public class HashProvider extends AbstractProvider {

    public HashProvider(XkJava app) {
        super(app);
    }

    @Override
    public void register() {
        this.app.singleton(
                Hash.class,
                (container, with) -> new Hash(),
                "crypt"
            );
    }

    @Override
    public void boot() {
        this.app.make(Hash.class);
    }
}
