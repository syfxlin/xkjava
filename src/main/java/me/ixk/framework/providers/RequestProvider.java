package me.ixk.framework.providers;

import me.ixk.framework.annotations.ScopeType;
import me.ixk.framework.http.CookieManager;
import me.ixk.framework.http.Request;
import me.ixk.framework.http.Response;
import me.ixk.framework.http.SessionManager;
import me.ixk.framework.ioc.Application;
import me.ixk.framework.kernel.Auth;
import me.ixk.framework.servlet.DispatcherServlet;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

// @Provider
// @Order(Order.HIGHEST_PRECEDENCE + 11)
public class RequestProvider extends AbstractProvider {

    public RequestProvider(Application app) {
        super(app);
    }

    @Override
    public void register() {
        this.app.bind(
                DispatcherServlet.class,
                DispatcherServlet.class,
                "dispatcherServlet",
                ScopeType.REQUEST
            );
        this.app.bind(
                HttpServlet.class,
                HttpServlet.class,
                "httpServlet",
                ScopeType.REQUEST
            );

        this.app.bind(
                Request.class,
                Request.class,
                "request",
                ScopeType.REQUEST
            );
        this.app.bind(
                HttpServletRequest.class,
                HttpServletRequest.class,
                "httpServletRequest",
                ScopeType.REQUEST
            );
        this.app.bind(
                Response.class,
                Response.class,
                "response",
                ScopeType.REQUEST
            );
        this.app.bind(
                HttpServletResponse.class,
                HttpServletResponse.class,
                "httpServletResponse",
                ScopeType.REQUEST
            );
        this.app.bind(
                CookieManager.class,
                CookieManager.class,
                "cookieManager",
                ScopeType.REQUEST
            );
        this.app.bind(
                SessionManager.class,
                SessionManager.class,
                "sessionManager",
                ScopeType.REQUEST
            );
        this.app.bind(Auth.class, Auth.class, "auth", ScopeType.REQUEST);
    }
}
