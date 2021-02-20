/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.http;

import javax.servlet.http.Cookie;
import me.ixk.framework.annotation.Component;
import me.ixk.framework.annotation.Scope;
import me.ixk.framework.ioc.context.ScopeType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Cookie 管理器
 * <p>
 * 用于获取 Cookie 和临时设置 Cookie，临时设置的 Cookie 会在响应发出前才插入到 Response，所以在响应发出前都可以修改 Cookie 及其设置
 *
 * @author Otstar Lin
 * @date 2020/10/14 上午 9:12
 */
@Component(name = "cookieManager")
@Scope(ScopeType.REQUEST)
public class CookieManager {

    private static final Logger log = LoggerFactory.getLogger(
        CookieManager.class
    );

    private final Request request;
    private final Response response;

    /**
     * Only used cglib
     */
    @Deprecated
    public CookieManager() {
        this(null, null);
    }

    public CookieManager(final Request request, final Response response) {
        this.request = request;
        this.response = response;
    }

    public boolean has(final String name) {
        return this.request.hasCookie(name);
    }

    public Cookie get(final String name) {
        return this.request.cookie(name);
    }

    public Cookie get(final String name, final Cookie defaultValue) {
        return this.request.cookie(name, defaultValue);
    }

    /* ===================================== */

    public CookieManager put(final Cookie cookie) {
        if (log.isDebugEnabled()) {
            log.debug("Add cookie to queue: {}", cookie);
        }
        this.response.cookie(cookie);
        return this;
    }

    public CookieManager forever(final Cookie cookie) {
        cookie.setMaxAge(157788000);
        this.put(cookie);
        return this;
    }

    public CookieManager forget(final String name) {
        final SetCookie cookie = new SetCookie(name, "");
        cookie.setMaxAge(1);
        this.put(cookie);
        return this;
    }

    public CookieManager queue(final SetCookie cookie) {
        this.put(cookie);
        return this;
    }
}
