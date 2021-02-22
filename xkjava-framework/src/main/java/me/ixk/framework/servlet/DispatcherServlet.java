/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.servlet;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.Map.Entry;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import me.ixk.framework.annotation.web.Servlet;
import me.ixk.framework.exception.HttpException;
import me.ixk.framework.exception.ResponseException;
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
import me.ixk.framework.websocket.WebSocketFactory;
import me.ixk.framework.websocket.WebSocketHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Servlet 调度器
 *
 * @author Otstar Lin
 * @date 2020/10/14 下午 4:54
 */
@Servlet(url = "/*", name = { "dispatcherServlet" }, asyncSupported = true)
@MultipartConfig
public class DispatcherServlet extends AbstractFrameworkServlet {

    private static final Logger log = LoggerFactory.getLogger(
        DispatcherServlet.class
    );
    private static final long serialVersionUID = -5890247928905581053L;

    private final XkJava app;
    private final RequestContext requestContext;
    private final SessionContext sessionContext;
    private final WebSocketFactory webSocketFactory;

    @Deprecated
    public DispatcherServlet(
        final XkJava app,
        final WebSocketFactory webSocketFactory
    ) {
        this.app = app;
        this.webSocketFactory = webSocketFactory;
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
    public void init() throws ServletException {
        super.init();
        try {
            this.webSocketFactory.start();
        } catch (Exception e) {
            log.error("WebSocketFactory start failed");
            throw new IllegalStateException(e);
        }
    }

    @Override
    public void destroy() {
        super.destroy();
        try {
            this.webSocketFactory.stop();
        } catch (Exception e) {
            log.error("WebSocketFactory stop failed");
            throw new IllegalStateException(e);
        }
    }

    @SuppressWarnings("unchecked")
    private void doDispatch(final Request request, final Response response)
        throws ServletException {
        final RouteDispatcher dispatcher = this.app.make(RouteDispatcher.class);
        final WebResolverRegistry webResolverRegistry =
            this.app.make(WebResolverRegistry.class);
        // 查询路由
        final RouteInfo routeInfo = dispatcher.dispatch(
            request.method(),
            request.path()
        );
        // 将 Route 信息设置到 Request
        request.setRoute(routeInfo);
        // 初始化 WebContext 和 WebAsyncManager
        final WebContext webContext = this.app.make(WebContext.class);
        final WebAsyncManager asyncManager = webContext.getAsyncManager();
        try {
            // 404 或 405 错误
            switch (routeInfo.getStatus()) {
                case NOT_FOUND:
                    throw new HttpException(HttpStatus.NOT_FOUND);
                case METHOD_NOT_ALLOWED:
                    throw new HttpException(HttpStatus.METHOD_NOT_ALLOWED);
                default:
            }
            // 设置 RequestAttribute 值
            this.setRequestAttributes(routeInfo, request);
            // 请求处理器
            final InvocableHandlerMethod handler;
            // 如果存在异步结果
            // 1. 首次请求
            // 2. 异步处理
            // 3. 将结果存入 WebAsyncManager
            // 4. Servlet 转发（相当于在内部再请求一次）
            // 5. 此时 asyncManager.hasConcurrentResult 就为 true
            final HandlerMethod handlerMethod = routeInfo.getHandler();
            if (asyncManager.hasConcurrentResult()) {
                // 如果存在异步结果，那么就使用异步响应的处理器
                handler =
                    new ConcurrentResultHandlerMethod(
                        this.app,
                        asyncManager.getConcurrentResult()
                    );
                handler.setMiddlewares(handlerMethod.getMiddlewares());
            } else {
                // 否则就取出请求处理器
                handler = new InvocableHandlerMethod(this.app, handlerMethod);
            }
            final HandlerMiddlewareChain handlerChain = new HandlerMiddlewareChain(
                handler
            );
            // 前置中间件
            if (!handlerChain.applyBeforeHandle(request, response)) {
                return;
            }
            // 处理请求
            Object returnValue = handlerChain.handle(request, response);
            // 如果是异步请求就无需处理后面的了
            if (asyncManager.isConcurrentHandlingStarted()) {
                return;
            }
            // 如果是 WebSocket 请求也无需处理
            if (
                handlerMethod instanceof WebSocketHandlerMethod &&
                this.webSocketFactory.isUpgradeRequest(request, response)
            ) {
                if (
                    this.webSocketFactory.accept(
                            (Class<? extends WebSocketHandler>) returnValue,
                            request,
                            response
                        )
                ) {
                    return;
                }
                return;
            }
            // Response 已提交就不管了
            if (response.isCommitted()) {
                return;
            }
            // 后置中间件
            returnValue =
                handlerChain.applyAfterHandle(returnValue, request, response);
            // 返回值解析
            this.processConvertResolver(
                    returnValue,
                    webContext,
                    routeInfo,
                    webResolverRegistry
                );
            // 完成中间件
            handlerChain.triggerAfterCompletion(request, response);
        } catch (final Throwable e) {
            // 异常处理
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
        final Method method = info.getHandler().getMethod();
        if (method == null) {
            return;
        }
        final Map<String, RequestAttributeDefinition> definitionMap = registry.getRegistry(
            method
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
