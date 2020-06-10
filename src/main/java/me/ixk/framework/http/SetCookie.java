package me.ixk.framework.http;

import java.util.concurrent.TimeUnit;
import javax.servlet.http.Cookie;

public class SetCookie extends Cookie {
    private long expiration;

    private boolean encrypt = true;

    public SetCookie(Cookie cookie) {
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

    public SetCookie(String name, String value) {
        this(name, value, -1);
    }

    public SetCookie(String name, String value, boolean httpOnly) {
        this(name, value, null, null, -1, httpOnly, false);
    }

    public SetCookie(String name, String value, String domain, String path) {
        this(name, value, domain, path, -1, false, false);
    }

    public SetCookie(String name, String value, int maxAge) {
        this(name, value, null, null, maxAge, false, false);
    }

    public SetCookie(
        String name,
        String value,
        String domain,
        String path,
        int maxAge,
        boolean httpOnly,
        boolean secure
    ) {
        this(name, value, domain, path, maxAge, httpOnly, secure, null, 0);
    }

    public SetCookie(
        String name,
        String value,
        String domain,
        String path,
        int maxAge,
        boolean httpOnly,
        boolean secure,
        String comment,
        int version
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

    @Override
    public void setMaxAge(int expiry) {
        super.setMaxAge(expiry);
        this.setExpiration();
    }

    public boolean isSecure() {
        return this.getSecure();
    }

    private void setExpiration() {
        int maxAge = this.getMaxAge();
        this.expiration =
            maxAge < 0
                ? -1
                : System.nanoTime() + TimeUnit.SECONDS.toNanos(maxAge);
    }

    public boolean isExpired(long timeNanos) {
        return expiration >= 0 && timeNanos >= expiration;
    }

    public boolean isEncrypt() {
        return encrypt;
    }

    public SetCookie setEncrypt(boolean encrypt) {
        this.encrypt = encrypt;
        return this;
    }
}