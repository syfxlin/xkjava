/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.helpers;

import me.ixk.framework.aop.ProxyCreator;
import me.ixk.framework.database.SqlSessionManager;
import me.ixk.framework.http.CookieManager;
import me.ixk.framework.http.Request;
import me.ixk.framework.http.Response;
import me.ixk.framework.http.SessionManager;
import me.ixk.framework.ioc.XkJava;
import me.ixk.framework.kernel.Environment;
import me.ixk.framework.route.RouteManager;
import me.ixk.framework.servlet.DispatcherServlet;
import me.ixk.framework.utils.Crypt;
import me.ixk.framework.utils.Hash;
import me.ixk.framework.utils.JWT;
import me.ixk.framework.web.WebContext;

public abstract class Facade {

    protected static <T> T make(Class<T> clazz) {
        return XkJava.of().make(clazz);
    }

    public static Environment env() {
        return make(Environment.class);
    }

    public static CookieManager cookie() {
        return make(CookieManager.class);
    }

    public static Crypt crypt() {
        return make(Crypt.class);
    }

    public static SqlSessionManager db() {
        return make(SqlSessionManager.class);
    }

    public static Hash hash() {
        return make(Hash.class);
    }

    public static JWT jwt() {
        return make(JWT.class);
    }

    public static Request request() {
        return make(Request.class);
    }

    public static Response response() {
        return make(Response.class);
    }

    public static RouteManager route() {
        return make(RouteManager.class);
    }

    public static SessionManager session() {
        return make(SessionManager.class);
    }

    public static WebContext context() {
        return make(WebContext.class);
    }

    public static DispatcherServlet servlet() {
        return make(DispatcherServlet.class);
    }

    /* ===================== */

    public static Object proxy(
        Object target,
        Class<?> targetType,
        Class<?>[] interfaces,
        Class<?>[] argsTypes,
        Object[] args
    ) {
        return ProxyCreator.createProxy(
            target,
            targetType,
            interfaces,
            argsTypes,
            args
        );
    }
}
