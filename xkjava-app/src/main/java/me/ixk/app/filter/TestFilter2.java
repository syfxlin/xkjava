/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.app.filter;

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import lombok.extern.slf4j.Slf4j;
import me.ixk.framework.annotation.Order;

/**
 * @author Otstar Lin
 * @date 2020/10/30 下午 10:33
 */
// @Filter(url = "/*")
@Order(Order.HIGHEST_PRECEDENCE)
@Slf4j
public class TestFilter2 implements javax.servlet.Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        log.info("TestFilter2 init");
    }

    @Override
    public void doFilter(
        ServletRequest request,
        ServletResponse response,
        FilterChain chain
    )
        throws IOException, ServletException {
        log.info("TestFilter2 doFilter");
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        log.info("TestFilter2 destroy");
    }
}
