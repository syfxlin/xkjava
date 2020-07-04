package me.ixk.framework.http;

import me.ixk.framework.ioc.Application;
import me.ixk.framework.kernel.Config;
import me.ixk.framework.kernel.Environment;
import me.ixk.framework.servlet.DispatcherServlet;

public class WebContext {
    private Application application;
    private Config config;
    private Environment environment;
    private DispatcherServlet servlet;
    private Request request;
    private Response response;
    private CookieManager cookieManager;
    private SessionManager sessionManager;

    @Deprecated
    public WebContext() {
        // only cglib
    }

    public WebContext(Application application) {
        this.application = application;
        this.config = application.make(Config.class);
        this.environment = application.make(Environment.class);
        this.servlet = application.make(DispatcherServlet.class);
        this.request = application.make(Request.class);
        this.response = application.make(Response.class);
        this.cookieManager = application.make(CookieManager.class);
        this.sessionManager = application.make(SessionManager.class);
    }

    public Application getApplication() {
        return application;
    }

    public Config getConfig() {
        return config;
    }

    public Environment getEnvironment() {
        return environment;
    }

    public DispatcherServlet getServlet() {
        return servlet;
    }

    public Request getRequest() {
        return request;
    }

    public Response getResponse() {
        return response;
    }

    public CookieManager getCookieManager() {
        return cookieManager;
    }

    public SessionManager getSessionManager() {
        return sessionManager;
    }
}
