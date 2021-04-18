/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.ioc.context;

import com.alibaba.ttl.TransmittableThreadLocal;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
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
public class RequestContext implements ThreadLocalContext {

    private static final Logger log = LoggerFactory.getLogger(
        RequestContext.class
    );
    private static final String INSTANCE_ATTRIBUTE_NAME =
        RequestContext.class.getName() + ".INSTANCE_ATTRIBUTE_NAME";
    private final TransmittableThreadLocal<HttpServletRequest> request = new TransmittableThreadLocal<>();

    @Override
    public void removeContext() {
        if (log.isDebugEnabled()) {
            log.debug("Remove request context");
        }
        this.request.remove();
    }

    @Override
    public Object getContext() {
        if (this.isCreated()) {
            return this.request.get();
        }
        throw new NullPointerException("RequestContext not created");
    }

    @Override
    public void setContext(final Object request) {
        if (log.isDebugEnabled()) {
            log.debug("Set request context");
        }
        if (request instanceof HttpServletRequest) {
            this.request.set((HttpServletRequest) request);
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
    @SuppressWarnings("unchecked")
    public ConcurrentMap<String, Object> getInstances() {
        final HttpServletRequest context = (HttpServletRequest) this.getContext();
        ConcurrentMap<String, Object> instances = (ConcurrentMap<String, Object>) context.getAttribute(
            INSTANCE_ATTRIBUTE_NAME
        );
        if (instances == null) {
            instances = new ConcurrentHashMap<>(50);
            context.setAttribute(INSTANCE_ATTRIBUTE_NAME, instances);
        }
        return instances;
    }
}
