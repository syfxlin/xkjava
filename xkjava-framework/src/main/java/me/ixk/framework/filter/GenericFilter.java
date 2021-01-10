/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.filter;

import java.util.Enumeration;
import javax.servlet.Filter;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;

/**
 * 通用过滤器
 *
 * @author Otstar Lin
 * @date 2020/10/30 下午 10:57
 */
public abstract class GenericFilter implements Filter {

    protected FilterConfig filterConfig;

    @Override
    public final void init(final FilterConfig filterConfig) {
        this.filterConfig = filterConfig;
        this.doInit(filterConfig);
    }

    @Override
    public final void destroy() {
        this.doDestroy(filterConfig);
    }

    public void doInit(final FilterConfig filterConfig) {}

    public void doDestroy(final FilterConfig filterConfig) {}

    public FilterConfig getFilterConfig() {
        return filterConfig;
    }

    public String getFilterName() {
        return filterConfig == null ? null : filterConfig.getFilterName();
    }

    public Enumeration<String> getInitParameterNames() {
        return filterConfig == null
            ? null
            : filterConfig.getInitParameterNames();
    }

    public ServletContext getServletContext() {
        return filterConfig == null ? null : filterConfig.getServletContext();
    }

    public String getInitParameter(String name) {
        return filterConfig == null
            ? null
            : filterConfig.getInitParameter(name);
    }
}
