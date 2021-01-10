/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.http;

import javax.servlet.http.Cookie;

/**
 * Cookie 单对象
 *
 * @author Otstar Lin
 * @date 2020/10/14 上午 10:28
 */
public class SetCookie extends Cookie {

    private static final long serialVersionUID = -3838921053220689617L;

    public SetCookie(final Cookie cookie) {
        this(
            cookie.getName(),
            cookie.getValue(),
            cookie.getDomain(),
            cookie.getPath(),
            cookie.getMaxAge(),
            cookie.isHttpOnly(),
            cookie.getSecure(),
            cookie.getComment(),
            cookie.getVersion()
        );
    }

    public SetCookie(final String name, final String value) {
        this(name, value, -1);
    }

    public SetCookie(
        final String name,
        final String value,
        final boolean httpOnly
    ) {
        this(name, value, null, null, -1, httpOnly, false);
    }

    public SetCookie(
        final String name,
        final String value,
        final String domain,
        final String path
    ) {
        this(name, value, domain, path, -1, false, false);
    }

    public SetCookie(final String name, final String value, final int maxAge) {
        this(name, value, null, null, maxAge, false, false);
    }

    public SetCookie(
        final String name,
        final String value,
        final String domain,
        final String path,
        final int maxAge,
        final boolean httpOnly,
        final boolean secure
    ) {
        this(name, value, domain, path, maxAge, httpOnly, secure, null, 0);
    }

    public SetCookie(
        final String name,
        final String value,
        final String domain,
        final String path,
        final int maxAge,
        final boolean httpOnly,
        final boolean secure,
        final String comment,
        final int version
    ) {
        super(name, value);
        if (domain != null) {
            this.setDomain(domain);
        }
        if (path != null) {
            this.setPath(path);
        }
        this.setMaxAge(maxAge);
        this.setHttpOnly(httpOnly);
        this.setSecure(secure);
        if (comment != null) {
            this.setComment(comment);
        }
        this.setVersion(version);
    }

    public SetCookie value(final String value) {
        this.setValue(value);
        return this;
    }

    public SetCookie domain(final String domain) {
        this.setDomain(domain);
        return this;
    }

    public SetCookie path(final String uri) {
        this.setPath(uri);
        return this;
    }

    public SetCookie maxAge(final int maxAge) {
        this.setMaxAge(maxAge);
        return this;
    }

    public SetCookie httpOnly(final boolean isHttpOnly) {
        this.setHttpOnly(isHttpOnly);
        return this;
    }

    public SetCookie secure(final boolean isSecure) {
        this.setSecure(isSecure);
        return this;
    }

    public SetCookie comment(final String comment) {
        this.setComment(comment);
        return this;
    }

    public SetCookie version(final int version) {
        this.setVersion(version);
        return this;
    }

    public String value() {
        return this.getValue();
    }

    public String domain() {
        return this.getDomain();
    }

    public String path() {
        return this.getPath();
    }

    public int maxAge() {
        return this.getMaxAge();
    }

    public boolean httpOnly() {
        return this.isHttpOnly();
    }

    public boolean secure() {
        return this.getSecure();
    }

    public String comment() {
        return this.getComment();
    }

    public int version() {
        return this.getVersion();
    }
}
