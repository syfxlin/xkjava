package me.ixk.framework.http;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.servlet.http.Cookie;
import org.eclipse.jetty.http.HttpCookie;

public class CookieManager {
    protected Map<String, Cookie> _requestCookies;

    protected Map<String, HttpCookie> _cookies;

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

    public void put(HttpCookie cookie) {
        this._cookies.put(cookie.getName(), cookie);
    }

    public void forever(HttpCookie cookie) {
        HttpCookie newCookie = new HttpCookie(
            cookie.getName(),
            cookie.getValue(),
            cookie.getDomain(),
            cookie.getPath(),
            2628000,
            cookie.isHttpOnly(),
            cookie.isSecure(),
            cookie.getComment(),
            cookie.getVersion()
        );
        this.put(newCookie);
    }

    public void forget(String name) {
        this.put(new HttpCookie(name, "", 1));
    }

    public boolean hasQueue(String name) {
        return this._cookies.containsKey(name);
    }

    public void unqueue(String name) {
        this._cookies.remove(name);
    }

    public void queue(HttpCookie cookie) {
        this.put(cookie);
    }

    public HttpCookie queued(String name) {
        return this.queued(name, null);
    }

    public HttpCookie queued(String name, HttpCookie cookie) {
        return this._cookies.getOrDefault(name, cookie);
    }

    public Map<String, HttpCookie> getQueues() {
        return this._cookies;
    }
}
