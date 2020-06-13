package me.ixk.framework.kernel;

import me.ixk.framework.http.Request;
import me.ixk.framework.ioc.Application;
import me.ixk.framework.ioc.RequestContext;
import me.ixk.framework.middleware.Handler;

public class ControllerHandler implements Handler {
    final String handler;

    public ControllerHandler(String handler) {
        this.handler = handler;
    }

    @Override
    public Object handle(Request request) {
        RequestContext.currentAttributes().setHandler(this.handler);
        Application app = Application.get();
        app.setGlobalArgs(request.all());
        Object result = app.call(this.handler, Object.class);
        app.clearGlobalArgs();
        return result;
    }
}
