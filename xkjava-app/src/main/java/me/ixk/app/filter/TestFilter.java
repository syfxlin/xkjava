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
import me.ixk.framework.annotation.core.Autowired;
import me.ixk.framework.annotation.core.Order;
import me.ixk.framework.ioc.XkJava;

/**
 * @author Otstar Lin
 * @date 2020/10/30 下午 9:16
 */
// @Filter(url = "/*")
@Order(Order.LOWEST_PRECEDENCE)
@Slf4j
public class TestFilter implements javax.servlet.Filter {

    @Autowired
    public XkJava xkJava;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        log.info("TestFilter init");
    }

    @Override
    public void doFilter(
        ServletRequest request,
        ServletResponse response,
        FilterChain chain
    ) throws IOException, ServletException {
        log.info("TestFilter doFilter");
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        log.info("TestFilter destroy");
    }
}
