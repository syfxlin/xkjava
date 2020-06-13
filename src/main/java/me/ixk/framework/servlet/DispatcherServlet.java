package me.ixk.framework.servlet;

import static me.ixk.framework.ioc.RequestContext.currentAttributes;
import static me.ixk.framework.ioc.RequestContext.resetAttributes;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import me.ixk.framework.exceptions.DispatchServletException;
import me.ixk.framework.exceptions.Exception;
import me.ixk.framework.factory.ObjectFactory;
import me.ixk.framework.http.*;
import me.ixk.framework.ioc.Application;
import me.ixk.framework.ioc.RequestContext;
import me.ixk.framework.kernel.Auth;
import me.ixk.framework.kernel.ExceptionHandlerResolver;
import me.ixk.framework.route.RouteManager;

public class DispatcherServlet extends FrameworkServlet {
    protected final Application app;

    @Deprecated
    public DispatcherServlet() {
        super();
        this.app = Application.get();
    }

    @Override
    public void init() throws ServletException {
        super.init();
    }

    @Override
    public void destroy() {
        super.destroy();
    }

    @Override
    protected void dispatch(Request request, Response response) {
        try {
            this.beforeDispatch(request, response);
            this.doDispatch(request, response);
        } catch (Throwable e) {
            if (!this.processException(e, request, response)) {
                throw e;
            }
        } finally {
            this.afterDispatch(request, response);
        }
    }

    protected void beforeDispatch(Request request, Response response) {
        // 利用 ThreadLocal 实现线程安全
        RequestContext requestContext = RequestContext.create();
        requestContext.setDispatcherServlet(this);
        requestContext.setHttpServlet(this);
        requestContext.setRequest(request);
        requestContext.setHttpServletRequest(request);
        requestContext.setResponse(response);
        requestContext.setHttpServletResponse(response);
        Cookie[] cookies = request.getCookies();
        requestContext.setCookieManager(
            new CookieManager(cookies == null ? new Cookie[0] : cookies)
        );
        requestContext.setSessionManager(
            new SessionManager(
                request.getSession(),
                request.getSessionManager()
            )
        );
        requestContext.setAuth(new Auth());

        this.app.instance(
                DispatcherServlet.class,
                (ObjectFactory<DispatcherServlet>) () ->
                    currentAttributes().getDispatcherServlet(),
                "dispatcherServlet"
            );
        this.app.instance(
                HttpServlet.class,
                (ObjectFactory<HttpServlet>) () ->
                    currentAttributes().getHttpServlet(),
                "httpServlet"
            );

        this.app.instance(
                Request.class,
                (ObjectFactory<Request>) () -> currentAttributes().getRequest(),
                "request"
            );
        this.app.instance(
                HttpServletRequest.class,
                (ObjectFactory<HttpServletRequest>) () ->
                    currentAttributes().getHttpServletRequest(),
                "httpServletRequest"
            );
        this.app.instance(
                Response.class,
                (ObjectFactory<Response>) () ->
                    currentAttributes().getResponse(),
                "response"
            );
        this.app.instance(
                HttpServletResponse.class,
                (ObjectFactory<HttpServletResponse>) () ->
                    currentAttributes().getHttpServletResponse(),
                "httpServletResponse"
            );
        this.app.instance(
                CookieManager.class,
                (ObjectFactory<CookieManager>) () ->
                    currentAttributes().getCookieManager()
            );
        this.app.instance(
                SessionManager.class,
                (ObjectFactory<SessionManager>) () ->
                    currentAttributes().getSessionManager()
            );
        this.app.instance(
                Auth.class,
                (ObjectFactory<Auth>) () -> currentAttributes().getAuth()
            );
    }

    protected void doDispatch(Request request, Response response) {
        this.app.make(RouteManager.class).dispatch(request, response);
    }

    protected void afterDispatch(Request request, Response response) {
        this.app.remove(DispatcherServlet.class);
        this.app.remove(HttpServlet.class);
        this.app.remove(Request.class);
        this.app.remove(HttpServletRequest.class);
        this.app.remove(Response.class);
        this.app.remove(HttpServletResponse.class);
        this.app.remove(CookieManager.class);
        this.app.remove(SessionManager.class);
        this.app.remove(Auth.class);
        resetAttributes();
    }

    @SuppressWarnings("unchecked")
    protected boolean processException(
        Throwable exception,
        Request request,
        Response response
    ) {
        Map<Class<?>, ExceptionHandlerResolver> controllerResolvers =
            this.app.getAttribute(
                    "controllerExceptionHandlerResolvers",
                    Map.class
                );
        if (
            controllerResolvers.containsKey(currentAttributes().getController())
        ) {
            if (
                this.processException(
                        exception,
                        request,
                        response,
                        controllerResolvers.entrySet()
                    )
            ) {
                return true;
            }
        }
        Map<Class<?>, ExceptionHandlerResolver> handlerResolvers =
            this.app.getAttribute("adviceExceptionHandlerResolvers", Map.class);
        return this.processException(
                exception,
                request,
                response,
                handlerResolvers.entrySet()
            );
    }

    protected boolean processException(
        Throwable exception,
        Request request,
        Response response,
        Set<Map.Entry<Class<?>, ExceptionHandlerResolver>> entrySet
    ) {
        for (Map.Entry<Class<?>, ExceptionHandlerResolver> entry : entrySet) {
            try {
                Method method = entry.getValue().resolveMethod(exception);
                if (method != null) {
                    // 绑定可能注入的异常
                    Map<String, Object> args = new ConcurrentHashMap<>();
                    args.put("exception", exception);
                    args.put(exception.getClass().getName(), exception);
                    args.put(Throwable.class.getName(), exception);
                    args.put(java.lang.Exception.class.getName(), exception);
                    args.put(Exception.class.getName(), exception);
                    // 重置响应
                    response.reset();
                    // 获取返回值
                    Object result =
                        this.app.call(
                                entry.getKey(),
                                method,
                                Object.class,
                                args
                            );
                    // 转换到 Response
                    ResponseProcessor.toResponse(request, response, result);
                    // Response 预处理
                    ResponseProcessor.dispatchResponse(response);
                    return true;
                }
            } catch (Throwable e) {
                throw new DispatchServletException(
                    "Process ExceptionHandlerResolver failed",
                    e
                );
            }
        }
        return false;
    }
}
