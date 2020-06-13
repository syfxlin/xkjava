package me.ixk.framework.facades;

import java.util.Map;
import me.ixk.framework.http.CookieManager;
import me.ixk.framework.http.SetCookie;

public class Cookie extends AbstractFacade {

    protected static CookieManager make() {
        return app.make(CookieManager.class);
    }

    public static boolean has(String name) {
        return make().has(name);
    }

    public static javax.servlet.http.Cookie get(String name) {
        return make().get(name);
    }

    public static javax.servlet.http.Cookie get(
        String name,
        SetCookie _default
    ) {
        return make().get(name, _default);
    }

    public static void put(SetCookie cookie) {
        make().put(cookie);
    }

    public static void forever(SetCookie cookie) {
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

    public static void queue(SetCookie cookie) {
        make().queue(cookie);
    }

    public static SetCookie queued(String name) {
        return make().queued(name);
    }

    public static SetCookie queued(String name, SetCookie cookie) {
        return make().queued(name, cookie);
    }

    public static Map<String, SetCookie> getQueues() {
        return make().getQueues();
    }
}
