/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.facades;

import me.ixk.framework.ioc.Application;

public abstract class AbstractFacade {
    protected static Application app;

    public static void setApplication(Application application) {
        app = application;
    }
}
