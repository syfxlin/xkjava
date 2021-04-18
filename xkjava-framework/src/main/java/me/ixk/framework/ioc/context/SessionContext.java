/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.ioc.context;

import com.alibaba.ttl.TransmittableThreadLocal;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import javax.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * SessionContext
 *
 * @author Otstar Lin
 * @date 2020/10/26 下午 9:36
 */
public class SessionContext implements ThreadLocalContext {

    private static final Logger log = LoggerFactory.getLogger(
        SessionContext.class
    );
    private static final String INSTANCE_ATTRIBUTE_NAME =
        SessionContext.class.getName() + ".INSTANCE_ATTRIBUTE_NAME";

    private final TransmittableThreadLocal<HttpSession> session = new TransmittableThreadLocal<>();

    @Override
    public void removeContext() {
        if (log.isDebugEnabled()) {
            log.debug("Remove session context");
        }
        this.session.remove();
    }

    @Override
    public Object getContext() {
        if (this.isCreated()) {
            return this.session.get();
        }
        throw new NullPointerException("SessionContext not created");
    }

    @Override
    public void setContext(final Object session) {
        if (log.isDebugEnabled()) {
            log.debug("Set session context");
        }
        if (session instanceof HttpSession) {
            this.session.set((HttpSession) session);
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
    @SuppressWarnings("unchecked")
    public ConcurrentMap<String, Object> getInstances() {
        final HttpSession context = (HttpSession) this.getContext();
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
