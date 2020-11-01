/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.ioc.context;

import javax.servlet.http.HttpSession;
import me.ixk.framework.annotations.ScopeType;
import me.ixk.framework.factory.ObjectFactory;
import me.ixk.framework.ioc.SessionAttributeContext;
import me.ixk.framework.utils.ReflectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * SessionContext
 *
 * @author Otstar Lin
 * @date 2020/10/26 下午 9:36
 */
public class SessionContext implements SessionAttributeContext {

    private static final Logger log = LoggerFactory
        .getLogger(SessionContext.class);

    private final ThreadLocal<HttpSession> session = new InheritableThreadLocal<>();

    @Override
    public void removeContext() {
        log.info("Remove session context");
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
        log.info("Set session context");
        this.session.set(session);
    }

    @Override
    public boolean isCreated() {
        return this.session.get() != null;
    }

    @Override
    public boolean matchesScope(final ScopeType scopeType) {
        return scopeType == ScopeType.SESSION;
    }

    @Override
    public Object get(final String name) {
        return this.getNotProxy(name);
    }

    @Override
    public Object get(final String name, final Class<?> returnType) {
        return ReflectUtils.proxyObjectFactory(
            (ObjectFactory<Object>) () -> this.getNotProxy(name), returnType);
    }

    public Object getNotProxy(final String name) {
        return this.getInstances().get(name);
    }
}
