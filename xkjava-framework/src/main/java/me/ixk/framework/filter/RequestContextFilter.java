/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.filter;

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import me.ixk.framework.annotations.Filter;
import me.ixk.framework.annotations.Order;
import me.ixk.framework.http.Request;
import me.ixk.framework.http.Response;
import me.ixk.framework.ioc.XkJava;
import me.ixk.framework.ioc.context.RequestContext;
import me.ixk.framework.ioc.context.ScopeType;
import me.ixk.framework.ioc.context.SessionContext;

/**
 * 请求作用域 Context 过滤器
 *
 * @author Otstar Lin
 * @date 2020/10/30 下午 11:28
 */
@Filter(url = "/*")
@Order(Order.HIGHEST_PRECEDENCE + 2)
public class RequestContextFilter extends GenericFilter {

    protected final XkJava app;
    protected final RequestContext requestContext;
    protected final SessionContext sessionContext;

    public RequestContextFilter(XkJava app) {
        this.app = app;
        this.requestContext =
            (RequestContext) this.app.getContextByScope(ScopeType.REQUEST);
        this.sessionContext =
            (SessionContext) this.app.getContextByScope(ScopeType.SESSION);
    }

    @Override
    public void doFilter(
        ServletRequest request,
        ServletResponse response,
        FilterChain chain
    ) throws IOException, ServletException {
        if (
            request instanceof HttpServletRequest &&
            response instanceof HttpServletResponse
        ) {
            this.initContext(
                    (HttpServletRequest) request,
                    (HttpServletResponse) response
                );
            try {
                chain.doFilter(request, response);
            } finally {
                this.resetContext();
            }
        } else {
            throw new ServletException(
                "RequestWrapperFilter just supports HTTP requests"
            );
        }
    }

    public void initContext(
        HttpServletRequest request,
        HttpServletResponse response
    ) {
        this.requestContext.setContext(request);
        this.sessionContext.setContext(request.getSession());
        this.app.setInstanceValue(Request.class, request);
        this.app.setInstanceValue(Response.class, response);
    }

    public void resetContext() {
        this.requestContext.removeContext();
        this.sessionContext.removeContext();
    }
}
