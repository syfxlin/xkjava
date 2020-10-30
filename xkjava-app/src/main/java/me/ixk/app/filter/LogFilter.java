/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.app.filter;

import java.io.IOException;
import java.time.LocalDateTime;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import me.ixk.app.entity.Logs;
import me.ixk.app.service.impl.LogsServiceImpl;
import me.ixk.framework.ioc.XkJava;

// @WebFilter("/login")
@Slf4j
public class LogFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        //
    }

    @Override
    public void doFilter(
        ServletRequest request,
        ServletResponse response,
        FilterChain chain
    )
        throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        if ("POST".equals(httpServletRequest.getMethod())) {
            LogsServiceImpl logsService = XkJava
                .of()
                .make(LogsServiceImpl.class);
            String ip = httpServletRequest.getRemoteAddr();
            LocalDateTime loginTime = LocalDateTime.now();
            log.info("User login: {} {}", ip, loginTime);
            logsService.save(
                Logs.builder().ip(ip).loginTime(loginTime).build()
            );
        }
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        //
    }
}
