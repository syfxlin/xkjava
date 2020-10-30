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

/**
 * 包装 Request Response 过滤器
 *
 * @author Otstar Lin
 * @date 2020/10/30 下午 11:30
 */
@Filter(url = "/*")
@Order(Order.HIGHEST_PRECEDENCE + 1)
public class RequestWrapperFilter extends GenericFilter {

    @Override
    public void doFilter(
        ServletRequest request,
        ServletResponse response,
        FilterChain chain
    )
        throws IOException, ServletException {
        if (
            request instanceof HttpServletRequest &&
            response instanceof HttpServletResponse
        ) {
            chain.doFilter(
                new Request((HttpServletRequest) request),
                new Response((HttpServletResponse) response)
            );
        } else {
            throw new ServletException(
                "RequestWrapperFilter just supports HTTP requests"
            );
        }
    }
}
