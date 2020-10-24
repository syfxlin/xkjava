/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.http;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.servlet.http.Cookie;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Cookie 管理器
 * <p>
 * 用于获取 Cookie 和临时设置 Cookie，临时设置的 Cookie 会在响应发出前才插入到 Response，所以在响应发出前都可以修改
 * Cookie 及其设置
 *
 * @author Otstar Lin
 * @date 2020/10/14 上午 9:12
 */
public class CookieManager {
    private static final Logger log = LoggerFactory.getLogger(
        CookieManager.class
    );

    protected Map<String, Cookie> requestCookies;

    protected Map<String, SetCookie> cookies;

    /**
     * Only used cglib
     */
    @Deprecated
    public CookieManager() {}

    public CookieManager(Cookie[] cookies) {
        this.requestCookies = new ConcurrentHashMap<>();
        for (Cookie cookie : cookies) {
            this.requestCookies.put(cookie.getName(), cookie);
        }
        this.cookies = new ConcurrentHashMap<>();
    }

    public boolean has(String name) {
        return this.requestCookies.containsKey(name);
    }

    public Cookie get(String name) {
        return this.requestCookies.get(name);
    }

    public Cookie get(String name, Cookie defaultValue) {
        return this.requestCookies.getOrDefault(name, defaultValue);
    }

    /* ===================================== */

    public CookieManager put(SetCookie cookie) {
        log.debug("Add cookie to queue: {}", cookie);
        this.cookies.put(cookie.getName(), cookie);
        return this;
    }

    public CookieManager forever(SetCookie cookie) {
        cookie.setMaxAge(157788000);
        this.put(cookie);
        return this;
    }

    public CookieManager forget(String name) {
        SetCookie cookie = new SetCookie(name, "");
        cookie.setMaxAge(1);
        this.put(cookie);
        return this;
    }

    public boolean hasQueue(String name) {
        return this.cookies.containsKey(name);
    }

    public CookieManager unqueue(String name) {
        this.cookies.remove(name);
        return this;
    }

    public CookieManager queue(SetCookie cookie) {
        this.put(cookie);
        return this;
    }

    public SetCookie queued(String name) {
        return this.queued(name, null);
    }

    public SetCookie queued(String name, SetCookie cookie) {
        return this.cookies.getOrDefault(name, cookie);
    }

    public Map<String, SetCookie> getQueues() {
        return this.cookies;
    }
}
