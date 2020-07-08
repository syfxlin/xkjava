/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.providers;

import me.ixk.framework.annotations.Order;
import me.ixk.framework.annotations.Provider;
import me.ixk.framework.annotations.ScopeType;
import me.ixk.framework.http.result.ViewResult;
import me.ixk.framework.ioc.XkJava;

@Provider
@Order(Order.HIGHEST_PRECEDENCE + 5)
public class ViewProvider extends AbstractProvider {

    public ViewProvider(XkJava app) {
        super(app);
    }

    @Override
    public void register() {
        this.app.bind(ViewResult.class, ViewResult.class, "view", ScopeType.PROTOTYPE);
    }
}
