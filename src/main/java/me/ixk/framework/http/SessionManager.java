/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.http;

import cn.hutool.core.convert.Convert;
import me.ixk.framework.helpers.UtilHelper;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import java.util.Enumeration;
import java.util.List;

public class SessionManager {
    HttpSession _session;

    @Deprecated
    public SessionManager() {
        // only used cglib
    }

    public SessionManager(HttpSession session) {
        this._session = session;
    }

    public HttpSession getSession() {
        return _session;
    }

    public SessionManager setSession(HttpSession _session) {
        this._session = _session;
        return this;
    }

    public boolean has(String name) {
        return this._session.getAttribute(name) != null;
    }

    public <T> T get(String name, Class<T> returnType) {
        return this.get(name, returnType, null);
    }

    public <T> T get(String name, Class<T> returnType, T _default) {
        Object result = this._session.getAttribute(name);
        if (result == null) {
            return _default;
        }
        return Convert.convert(returnType, result);
    }

    public SessionManager put(String name, Object value) {
        this._session.setAttribute(name, value);
        return this;
    }

    public SessionManager forget(String name) {
        this._session.removeAttribute(name);
        return this;
    }

    public SessionManager forget(List<String> names) {
        for (String name : names) {
            this._session.removeAttribute(name);
            return this;
        }
        return this;
    }

    public SessionManager flush() {
        while (this._session.getAttributeNames().hasMoreElements()) {
            String s = this._session.getAttributeNames().nextElement();
            this._session.removeAttribute(s);
        }
        return this;
    }

    public <T> T pull(String name, Class<T> returnType, T _default) {
        T result = this.get(name, returnType, _default);
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
        String token = UtilHelper.strRandom(40);
        this.put("_token", token);
        return token;
    }

    public long getCreationTime() {
        return this._session.getCreationTime();
    }

    public String getId() {
        return this._session.getId();
    }

    public long getLastAccessedTime() {
        return this._session.getLastAccessedTime();
    }

    public ServletContext getServletContext() {
        return this._session.getServletContext();
    }

    public SessionManager setMaxInactiveInterval(int interval) {
        this._session.setMaxInactiveInterval(interval);
        return this;
    }

    public int getMaxInactiveInterval() {
        return this._session.getMaxInactiveInterval();
    }

    public Object getAttribute(String name) {
        return this._session.getAttribute(name);
    }

    public Enumeration<String> getAttributeNames() {
        return this._session.getAttributeNames();
    }

    public SessionManager setAttribute(String name, Object value) {
        this._session.setAttribute(name, value);
        return this;
    }

    public SessionManager removeAttribute(String name) {
        this._session.removeAttribute(name);
        return this;
    }

    public SessionManager invalidate() {
        this._session.invalidate();
        return this;
    }

    public boolean isNew() {
        return this._session.isNew();
    }
}
