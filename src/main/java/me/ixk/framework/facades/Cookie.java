package me.ixk.framework.facades;

import java.util.Map;
import me.ixk.framework.http.CookieManager;
import org.eclipse.jetty.http.HttpCookie;

public class Cookie extends AbstractFacade {

    protected static CookieManager make() {
        return app.make(CookieManager.class);
    }

    public static void refresh(javax.servlet.http.Cookie[] cookies) {
        make().refresh(cookies);
    }

    public static boolean has(String name) {
        return make().has(name);
    }

    public static javax.servlet.http.Cookie get(String name) {
        return make().get(name);
    }

    public static javax.servlet.http.Cookie get(
        String name,
        javax.servlet.http.Cookie _default
    ) {
        return make().get(name, _default);
    }

    public static void put(HttpCookie cookie) {
        make().put(cookie);
    }

    public static void forever(HttpCookie cookie) {
        make().forever(cookie);
    }

    public static void forget(String name) {
        make().forget(name);
    }

    public static boolean hasQueue(String name) {
        return make().hasQueue(name);
    }

    public static void unqueue(String name) {
        make().unqueue(name);
    }

    public static void queue(HttpCookie cookie) {
        make().queue(cookie);
    }

    public static HttpCookie queued(String name) {
        return make().queued(name);
    }

    public static HttpCookie queued(String name, HttpCookie cookie) {
        return make().queued(name, cookie);
    }

    public static Map<String, HttpCookie> getQueues() {
        return make().getQueues();
    }
}
