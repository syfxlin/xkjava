/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.helper;

import me.ixk.framework.aop.ProxyCreator;
import me.ixk.framework.database.SqlSessionManager;
import me.ixk.framework.http.CookieManager;
import me.ixk.framework.http.Request;
import me.ixk.framework.http.Response;
import me.ixk.framework.http.SessionManager;
import me.ixk.framework.ioc.XkJava;
import me.ixk.framework.property.Environment;
import me.ixk.framework.servlet.DispatcherServlet;
import me.ixk.framework.util.Crypt;
import me.ixk.framework.util.Hash;
import me.ixk.framework.util.Jwt;
import me.ixk.framework.web.WebContext;

/**
 * 门面工具类
 *
 * @author Otstar Lin
 * @date 2020/10/14 上午 9:09
 */
public class Facade {

    private static <T> T make(final Class<T> clazz) {
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

    public static Jwt jwt() {
        return make(Jwt.class);
    }

    public static Request request() {
        return make(Request.class);
    }

    public static Response response() {
        return make(Response.class);
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
        final Object target,
        final Class<?> targetType,
        final Class<?>[] interfaces,
        final Class<?>[] argsTypes,
        final Object[] args
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
