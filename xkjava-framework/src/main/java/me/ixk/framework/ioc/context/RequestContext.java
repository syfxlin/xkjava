/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.ioc.context;

import com.alibaba.ttl.TransmittableThreadLocal;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * RequestContext
 * <p>
 * 请求作用域的 Context
 *
 * @author Otstar Lin
 * @date 2020/10/14 上午 10:40
 */
public class RequestContext implements ThreadLocalContext<HttpServletRequest> {

    private static final Logger log = LoggerFactory.getLogger(
        RequestContext.class
    );

    private final TransmittableThreadLocal<HttpServletRequest> request = new TransmittableThreadLocal<>();

    @Override
    public void removeContext() {
        if (log.isDebugEnabled()) {
            log.debug("Remove request context");
        }
        this.request.remove();
    }

    @Override
    public HttpServletRequest getContext() {
        if (this.isCreated()) {
            return this.request.get();
        }
        throw new NullPointerException("RequestContext not created");
    }

    @Override
    public void setContext(final HttpServletRequest request) {
        if (log.isDebugEnabled()) {
            log.debug("Set request context");
        }
        if (request != null) {
            this.request.set(request);
        } else {
            throw new IllegalArgumentException(
                "RequestContext set context, value does not instanceof HttpServletRequest"
            );
        }
    }

    @Override
    public boolean isCreated() {
        return this.request.get() != null;
    }

    @Override
    public Object get(final String name) {
        final HttpServletRequest context = this.getContext();
        return context.getAttribute(name);
    }

    @Override
    public void remove(final String name) {
        final HttpServletRequest context = this.getContext();
        context.removeAttribute(name);
    }

    @Override
    public void set(final String name, final Object instance) {
        final HttpServletRequest context = this.getContext();
        context.setAttribute(name, instance);
    }

    @Override
    public boolean has(final String name) {
        final HttpServletRequest context = this.getContext();
        return context.getAttribute(name) != null;
    }
}
