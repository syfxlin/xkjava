/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.ioc.context;

import com.alibaba.ttl.TransmittableThreadLocal;
import javax.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * SessionContext
 *
 * @author Otstar Lin
 * @date 2020/10/26 下午 9:36
 */
public class SessionContext implements ThreadLocalContext<HttpSession> {

    private static final Logger log = LoggerFactory.getLogger(
        SessionContext.class
    );

    private final TransmittableThreadLocal<HttpSession> session = new TransmittableThreadLocal<>();

    @Override
    public void removeContext() {
        if (log.isDebugEnabled()) {
            log.debug("Remove session context");
        }
        this.session.remove();
    }

    @Override
    public HttpSession getContext() {
        if (this.isCreated()) {
            return this.session.get();
        }
        throw new NullPointerException("SessionContext not created");
    }

    @Override
    public void setContext(final HttpSession session) {
        if (log.isDebugEnabled()) {
            log.debug("Set session context");
        }
        if (session != null) {
            this.session.set(session);
        } else {
            throw new IllegalArgumentException(
                "SessionContext set context, value does not instanceof HttpSession"
            );
        }
    }

    @Override
    public boolean isCreated() {
        return this.session.get() != null;
    }

    @Override
    public Object get(final String name) {
        final HttpSession context = this.getContext();
        return context.getAttribute(name);
    }

    @Override
    public void remove(final String name) {
        final HttpSession context = this.getContext();
        context.removeAttribute(name);
    }

    @Override
    public void set(final String name, final Object instance) {
        final HttpSession context = this.getContext();
        context.setAttribute(name, instance);
    }

    @Override
    public boolean has(final String name) {
        final HttpSession context = this.getContext();
        return context.getAttribute(name) != null;
    }
}
