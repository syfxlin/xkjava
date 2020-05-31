package me.ixk.framework.http;

import me.ixk.framework.utils.Helper;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import java.util.Enumeration;
import java.util.List;

public class SessionManager {
    HttpSession _session;
    org.eclipse.jetty.server.SessionManager _sessionManager;

    public void setSession(
        HttpSession session,
        org.eclipse.jetty.server.SessionManager sessionManager
    ) {
        this._session = session;
        this._sessionManager = sessionManager;
    }

    public HttpSession getSession() {
        return _session;
    }

    public void setSession(HttpSession _session) {
        this._session = _session;
    }

    public org.eclipse.jetty.server.SessionManager getSessionManager() {
        return _sessionManager;
    }

    public void setSessionManager(
        org.eclipse.jetty.server.SessionManager _sessionManager
    ) {
        this._sessionManager = _sessionManager;
    }

    public boolean has(String name) {
        return this._session.getAttribute(name) != null;
    }

    public Object get(String name) {
        return this.get(name, null);
    }

    public Object get(String name, Object _default) {
        Object result = this._session.getAttribute(name);
        if (result == null) {
            return _default;
        }
        return result;
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

    public Object pull(String name, Object _default) {
        Object result = this.get(name, _default);
        this.forget(name);
        return result;
    }

    public String token() {
        return this.get("_token").toString();
    }

    public void regenerateToken() {
        this.put("_token", Helper.strRandom(40));
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