package me.ixk.framework.http;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.servlet.http.Cookie;

public class CookieManager {
    protected Map<String, Cookie> _requestCookies;

    protected Map<String, SetCookie> _cookies;

    public void refresh(Cookie[] cookies) {
        this._requestCookies = new ConcurrentHashMap<>();
        for (Cookie cookie : cookies) {
            this._requestCookies.put(cookie.getName(), cookie);
        }
        this._cookies = new ConcurrentHashMap<>();
    }

    public boolean has(String name) {
        return this._requestCookies.containsKey(name);
    }

    public Cookie get(String name) {
        return this._requestCookies.get(name);
    }

    public Cookie get(String name, Cookie _default) {
        return this._requestCookies.getOrDefault(name, _default);
    }

    /* ===================================== */

    public void put(SetCookie cookie) {
        this._cookies.put(cookie.getName(), cookie);
    }

    public void forever(SetCookie cookie) {
        cookie.setMaxAge(2628000);
        this.put(cookie);
    }

    public void forget(String name) {
        SetCookie cookie = new SetCookie(name, "");
        cookie.setMaxAge(1);
        this.put(cookie);
    }

    public boolean hasQueue(String name) {
        return this._cookies.containsKey(name);
    }

    public void unqueue(String name) {
        this._cookies.remove(name);
    }

    public void queue(SetCookie cookie) {
        this.put(cookie);
    }

    public SetCookie queued(String name) {
        return this.queued(name, null);
    }

    public SetCookie queued(String name, SetCookie cookie) {
        return this._cookies.getOrDefault(name, cookie);
    }

    public Map<String, SetCookie> getQueues() {
        return this._cookies;
    }
}
