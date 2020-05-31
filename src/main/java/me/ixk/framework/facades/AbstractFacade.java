package me.ixk.framework.facades;

import me.ixk.framework.ioc.Application;

public abstract class AbstractFacade {
    protected static Application app;

    public static void setApplication(Application application) {
        app = application;
    }

    public static <T> T make(Class<T> _class) {
        return app.make(_class);
    }
}