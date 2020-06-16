package me.ixk.framework.providers;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import me.ixk.framework.annotations.Order;
import me.ixk.framework.annotations.Provider;
import me.ixk.framework.annotations.ScopeType;
import me.ixk.framework.http.CookieManager;
import me.ixk.framework.http.Request;
import me.ixk.framework.http.Response;
import me.ixk.framework.http.SessionManager;
import me.ixk.framework.ioc.Application;
import me.ixk.framework.kernel.Auth;
import me.ixk.framework.servlet.DispatcherServlet;

@Provider
@Order(Order.HIGHEST_PRECEDENCE + 11)
public class RequestProvider extends AbstractProvider {

    public RequestProvider(Application app) {
        super(app);
    }

    @Override
    public void register() {
        this.app.bind(
                DispatcherServlet.class,
                DispatcherServlet.class,
                ScopeType.REQUEST,
                "dispatcherServlet"
            );
        this.app.bind(
                HttpServlet.class,
                HttpServlet.class,
                ScopeType.REQUEST,
                "httpServlet"
            );

        this.app.bind(
                Request.class,
                Request.class,
                ScopeType.REQUEST,
                "request"
            );
        this.app.bind(
                HttpServletRequest.class,
                HttpServletRequest.class,
                ScopeType.REQUEST,
                "httpServletRequest"
            );
        this.app.bind(
                Response.class,
                Response.class,
                ScopeType.REQUEST,
                "response"
            );
        this.app.bind(
                HttpServletResponse.class,
                HttpServletResponse.class,
                ScopeType.REQUEST,
                "httpServletResponse"
            );
        this.app.bind(
                CookieManager.class,
                CookieManager.class,
                ScopeType.REQUEST,
                "cookieManager"
            );
        this.app.bind(
                SessionManager.class,
                SessionManager.class,
                ScopeType.REQUEST,
                "sessionManager"
            );
        this.app.bind(Auth.class, Auth.class, ScopeType.REQUEST, "auth");
    }
}
