/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.servlet;

import java.util.Map;
import java.util.Map.Entry;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import me.ixk.framework.annotations.Servlet;
import me.ixk.framework.exceptions.HttpException;
import me.ixk.framework.exceptions.ResponseException;
import me.ixk.framework.http.HttpStatus;
import me.ixk.framework.http.Request;
import me.ixk.framework.http.Response;
import me.ixk.framework.ioc.XkJava;
import me.ixk.framework.ioc.context.RequestContext;
import me.ixk.framework.ioc.context.ScopeType;
import me.ixk.framework.ioc.context.SessionContext;
import me.ixk.framework.middleware.HandlerMiddlewareChain;
import me.ixk.framework.registry.after.WebResolverRegistry;
import me.ixk.framework.route.RouteDispatcher;
import me.ixk.framework.route.RouteInfo;
import me.ixk.framework.web.RequestAttributeRegistry;
import me.ixk.framework.web.RequestAttributeRegistry.RequestAttributeDefinition;
import me.ixk.framework.web.WebContext;
import me.ixk.framework.web.async.WebAsyncManager;
import me.ixk.framework.web.resolver.AfterHandlerExceptionResolver;
import me.ixk.framework.web.resolver.ResponseConvertResolver;

/**
 * Servlet 调度器
 *
 * @author Otstar Lin
 * @date 2020/10/14 下午 4:54
 */
@Servlet(url = "/*", name = { "dispatcherServlet" }, asyncSupported = true)
@MultipartConfig
public class DispatcherServlet extends AbstractFrameworkServlet {

    private static final long serialVersionUID = -5890247928905581053L;

    private final XkJava app;
    private final RequestContext requestContext;
    private final SessionContext sessionContext;

    @Deprecated
    public DispatcherServlet(final XkJava app) {
        this.app = app;
        this.requestContext =
            (RequestContext) this.app.getContextByScope(ScopeType.REQUEST);
        this.sessionContext =
            (SessionContext) this.app.getContextByScope(ScopeType.SESSION);
    }

    @Override
    protected void dispatch(
        final HttpServletRequest req,
        final HttpServletResponse resp
    ) throws ServletException {
        final Request request = new Request(req);
        final Response response = new Response(resp);
        this.initContext(request, response);
        try {
            this.doDispatch(request, response);
        } finally {
            this.resetContext();
        }
    }

    @Override
    public void destroy() {
        super.destroy();
    }

    @Override
    public void init() throws ServletException {
        super.init();
    }

    private void doDispatch(final Request request, final Response response)
        throws ServletException {
        final RouteDispatcher dispatcher = this.app.make(RouteDispatcher.class);
        final WebResolverRegistry webResolverRegistry =
            this.app.make(WebResolverRegistry.class);
        final RouteInfo routeInfo = dispatcher.dispatch(
            request.method(),
            request.path()
        );
        // 将 Route 信息设置到 Request
        request.setRoute(routeInfo);

        final WebContext webContext = this.app.make(WebContext.class);
        final WebAsyncManager asyncManager = webContext.getAsyncManager();
        try {
            switch (routeInfo.getStatus()) {
                case NOT_FOUND:
                    throw new HttpException(HttpStatus.NOT_FOUND);
                case METHOD_NOT_ALLOWED:
                    throw new HttpException(HttpStatus.METHOD_NOT_ALLOWED);
                default:
            }
            // 设置 RequestAttribute 值
            this.setRequestAttributes(routeInfo, request);
            final InvocableHandlerMethod handler;
            if (asyncManager.hasConcurrentResult()) {
                handler =
                    new ConcurrentResultHandlerMethod(
                        this.app,
                        asyncManager.getConcurrentResult()
                    );
                handler.setMiddlewares(routeInfo.getHandler().getMiddlewares());
            } else {
                handler =
                    new InvocableHandlerMethod(
                        this.app,
                        routeInfo.getHandler()
                    );
            }
            final HandlerMiddlewareChain handlerChain = new HandlerMiddlewareChain(
                handler
            );
            if (!handlerChain.applyBeforeHandle(request, response)) {
                return;
            }
            Object returnValue = handlerChain.handle(request, response);
            if (asyncManager.isConcurrentHandlingStarted()) {
                return;
            }
            returnValue =
                handlerChain.applyAfterHandle(returnValue, request, response);
            this.processConvertResolver(
                    returnValue,
                    webContext,
                    routeInfo,
                    webResolverRegistry
                );
            handlerChain.triggerAfterCompletion(request, response);
        } catch (final Throwable e) {
            final boolean resolved =
                this.processAfterException(e, webContext, webResolverRegistry);
            if (!resolved) {
                throw new ServletException(e);
            }
        }
    }

    private void initContext(final Request request, final Response response) {
        this.requestContext.setContext(request);
        this.sessionContext.setContext(request.getSession());
        this.app.setInstanceValue(Request.class, request);
        this.app.setInstanceValue(Response.class, response);
    }

    private void resetContext() {
        this.requestContext.removeContext();
        this.sessionContext.removeContext();
    }

    private void setRequestAttributes(
        final RouteInfo info,
        final Request request
    ) {
        final RequestAttributeRegistry registry =
            this.app.make(RequestAttributeRegistry.class);
        final Map<String, RequestAttributeDefinition> definitionMap = registry.getRegistry(
            info.getHandler().getMethod()
        );
        if (definitionMap != null) {
            for (final Entry<String, RequestAttributeDefinition> entry : definitionMap.entrySet()) {
                final String attributeName = entry.getKey();
                final RequestAttributeDefinition definition = entry.getValue();
                request.setAttribute(
                    attributeName,
                    definition
                        .getRegistry()
                        .register(
                            this.app,
                            attributeName,
                            definition.getMethod(),
                            definition.getAnnotation()
                        )
                );
            }
        }
    }

    private void processConvertResolver(
        final Object returnValue,
        final WebContext context,
        final RouteInfo info,
        final WebResolverRegistry webResolverRegistry
    ) {
        for (final ResponseConvertResolver converter : webResolverRegistry.getResponseConverters()) {
            if (converter.supportsConvert(returnValue, context, info)) {
                if (converter.resolveConvert(returnValue, context, info)) {
                    return;
                }
            }
        }
        throw new ResponseException(
            "The return value cannot be converted into a response. [" +
            returnValue.getClass() +
            "]"
        );
    }

    private boolean processAfterException(
        final Throwable exception,
        final WebContext context,
        final WebResolverRegistry webResolverRegistry
    ) {
        for (final AfterHandlerExceptionResolver resolver : webResolverRegistry.getAfterHandlerExceptionResolvers()) {
            final boolean resolved = resolver.resolveException(
                exception,
                context
            );
            if (resolved) {
                return true;
            }
        }
        return false;
    }
}
