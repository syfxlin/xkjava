package me.ixk.framework.servlet;

import cn.hutool.core.util.ReflectUtil;
import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.Callable;
import me.ixk.framework.http.Request;
import me.ixk.framework.http.Response;
import me.ixk.framework.middleware.Middleware;
import me.ixk.framework.util.ClassUtils;
import me.ixk.framework.web.Handler;

/**
 * @author Otstar Lin
 * @date 2021/1/11 下午 2:38
 */
public class HandlerMethod {

    private static final Method HANDLER_METHOD = ReflectUtil.getMethod(
        Handler.class,
        "handle",
        Request.class,
        Response.class
    );
    private static final Method CALLABLE_METHOD = ReflectUtil.getMethod(
        Callable.class,
        "call"
    );

    private final Object handler;
    private final Method method;
    private final Class<?> handlerType;
    private List<Middleware> middlewares;

    public HandlerMethod(final Object handler, final Method method) {
        this.handler = handler;
        this.method = method;
        this.handlerType = ClassUtils.getUserClass(handler.getClass());
    }

    public HandlerMethod(final String handler, final Method method) {
        this.handler = handler;
        this.method = method;
        this.handlerType = ClassUtils.getUserClass(method.getDeclaringClass());
    }

    public HandlerMethod(final Method method) {
        this.handlerType = ClassUtils.getUserClass(method.getDeclaringClass());
        this.handler = this.handlerType;
        this.method = method;
    }

    public HandlerMethod(final Handler handler) {
        this.handler = handler;
        this.method = HANDLER_METHOD;
        this.handlerType = Handler.class;
    }

    public HandlerMethod(final Callable<?> handler) {
        this.handler = handler;
        this.method = CALLABLE_METHOD;
        this.handlerType = Callable.class;
    }

    public HandlerMethod(final HandlerMethod handler) {
        this.handler = handler.handler;
        this.method = handler.method;
        this.middlewares = handler.middlewares;
        this.handlerType = handler.handlerType;
    }

    public Object getHandler() {
        return handler;
    }

    public Method getMethod() {
        return method;
    }

    public List<Middleware> getMiddlewares() {
        return middlewares;
    }

    public Class<?> getHandlerType() {
        return handlerType;
    }

    public void setMiddlewares(final List<Middleware> middlewares) {
        this.middlewares = middlewares;
    }
}
