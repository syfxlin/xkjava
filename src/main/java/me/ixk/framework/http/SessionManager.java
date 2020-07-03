package me.ixk.framework.http;

import cn.hutool.core.convert.Convert;
import java.util.Enumeration;
import java.util.List;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import me.ixk.framework.utils.Helper;

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

    public void setSession(HttpSession _session) {
        this._session = _session;
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

    public void put(String name, Object value) {
        this._session.setAttribute(name, value);
    }

    public void forget(String name) {
        this._session.removeAttribute(name);
    }

    public void forget(List<String> names) {
        for (String name : names) {
            this._session.removeAttribute(name);
        }
    }

    public void flush() {
        while (this._session.getAttributeNames().hasMoreElements()) {
            String s = this._session.getAttributeNames().nextElement();
            this._session.removeAttribute(s);
        }
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
        String token = Helper.strRandom(40);
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

    public void setMaxInactiveInterval(int interval) {
        this._session.setMaxInactiveInterval(interval);
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

    public void setAttribute(String name, Object value) {
        this._session.setAttribute(name, value);
    }

    public void removeAttribute(String name) {
        this._session.removeAttribute(name);
    }

    public void invalidate() {
        this._session.invalidate();
    }

    public boolean isNew() {
        return this._session.isNew();
    }
}
