/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.ioc.context;

import javax.servlet.http.HttpServletRequest;
import me.ixk.framework.annotations.ScopeType;
import me.ixk.framework.factory.ObjectFactory;
import me.ixk.framework.ioc.RequestAttributeContext;
import me.ixk.framework.utils.ReflectUtils;
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
public class RequestContext implements RequestAttributeContext {

    private static final Logger log = LoggerFactory
        .getLogger(RequestContext.class);
    private final ThreadLocal<HttpServletRequest> request = new InheritableThreadLocal<>();

    @Override
    public void removeContext() {
        log.info("Remove request context");
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
        log.info("Set request context");
        this.request.set(request);
    }

    @Override
    public boolean isCreated() {
        return this.request.get() != null;
    }

    @Override
    public boolean matchesScope(final ScopeType scopeType) {
        return scopeType == ScopeType.REQUEST;
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
