/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.http;

import java.util.Enumeration;
import java.util.List;
import java.util.UUID;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import me.ixk.framework.annotation.core.Component;
import me.ixk.framework.annotation.core.Scope;
import me.ixk.framework.ioc.context.ScopeType;
import me.ixk.framework.util.Convert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Session 管理器
 *
 * @author Otstar Lin
 * @date 2020/10/14 上午 10:18
 */
@Component(name = "sessionManager")
@Scope(ScopeType.REQUEST)
public class SessionManager {

    private static final Logger log = LoggerFactory.getLogger(
        SessionManager.class
    );

    private HttpSession session;

    /**
     * Only used cglib
     */
    @Deprecated
    public SessionManager() {}

    public SessionManager(Request request) {
        this.session = request.getSession();
    }

    public HttpSession session() {
        return session;
    }

    public boolean has(String name) {
        return this.session.getAttribute(name) != null;
    }

    public <T> T get(String name, Class<T> returnType) {
        return this.get(name, returnType, null);
    }

    public <T> T get(String name, Class<T> returnType, T defaultValue) {
        Object result = this.session.getAttribute(name);
        if (result == null) {
            return defaultValue;
        }
        return Convert.convert(returnType, result);
    }

    public SessionManager put(String name, Object value) {
        if (log.isDebugEnabled()) {
            log.debug("Add session: {} {}", name, value);
        }
        this.session.setAttribute(name, value);
        return this;
    }

    public SessionManager forget(String name) {
        if (log.isDebugEnabled()) {
            log.debug("Remove session: {}", name);
        }
        this.session.removeAttribute(name);
        return this;
    }

    public SessionManager forget(List<String> names) {
        if (log.isDebugEnabled()) {
            log.debug("Remove sessions: {}", String.join(",", names));
        }
        for (String name : names) {
            this.session.removeAttribute(name);
        }
        return this;
    }

    public SessionManager flush() {
        if (log.isDebugEnabled()) {
            log.debug("Flush session");
        }
        while (this.session.getAttributeNames().hasMoreElements()) {
            String s = this.session.getAttributeNames().nextElement();
            this.session.removeAttribute(s);
        }
        return this;
    }

    public <T> T pull(String name, Class<T> returnType, T defaultValue) {
        T result = this.get(name, returnType, defaultValue);
        this.forget(name);
        return result;
    }

    public String token() {
        String token = this.get("_token", String.class);
        if (token == null) {
            token = this.regenerateToken();
        }
        return token;
    }

    public String regenerateToken() {
        String token = UUID.randomUUID().toString();
        this.put("_token", token);
        return token;
    }

    public long creationTime() {
        return this.session.getCreationTime();
    }

    public String id() {
        return this.session.getId();
    }

    public long lastAccessedTime() {
        return this.session.getLastAccessedTime();
    }

    public ServletContext servletContext() {
        return this.session.getServletContext();
    }

    public int maxInactiveInterval() {
        return this.session.getMaxInactiveInterval();
    }

    public SessionManager maxInactiveInterval(int interval) {
        this.session.setMaxInactiveInterval(interval);
        return this;
    }

    public Object getAttribute(String name) {
        return this.session.getAttribute(name);
    }

    public Enumeration<String> getAttributeNames() {
        return this.session.getAttributeNames();
    }

    public SessionManager setAttribute(String name, Object value) {
        this.session.setAttribute(name, value);
        return this;
    }

    public SessionManager removeAttribute(String name) {
        this.session.removeAttribute(name);
        return this;
    }

    public SessionManager invalidate() {
        this.session.invalidate();
        return this;
    }

    public boolean isNew() {
        return this.session.isNew();
    }
}
