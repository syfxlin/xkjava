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
import me.ixk.framework.annotations.Autowired;
import me.ixk.framework.annotations.Filter;
import me.ixk.framework.annotations.Order;
import me.ixk.framework.web.CorsProcessor;
import me.ixk.framework.web.CorsProcessor.Configuration;

/**
 * CORS 过滤器
 *
 * @author Otstar Lin
 * @date 2020/10/30 下午 11:27
 */
@Filter(url = "/*", asyncSupported = true)
@Order(Order.HIGHEST_PRECEDENCE + 3)
public class CorsFilter extends GenericFilter {

    private final CorsProcessor corsProcessor;

    private Configuration configuration;

    @Autowired
    public CorsFilter(CorsProcessor corsProcessor) {
        this.corsProcessor = corsProcessor;
    }

    public CorsFilter(
        CorsProcessor corsProcessor,
        Configuration configuration
    ) {
        this.corsProcessor = corsProcessor;
        this.configuration = configuration;
    }

    @Override
    public void doFilter(
        ServletRequest request,
        ServletResponse response,
        FilterChain chain
    ) throws IOException, ServletException {
        if (this.configuration != null) {
            this.corsProcessor.processRequest(
                    this.configuration,
                    (HttpServletRequest) request,
                    (HttpServletResponse) response
                );
        }
        if (
            !this.corsProcessor.isPreFlightRequest((HttpServletRequest) request)
        ) {
            chain.doFilter(request, response);
        }
    }
}
