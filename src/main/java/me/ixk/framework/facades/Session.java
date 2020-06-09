package me.ixk.framework.facades;

import java.util.Enumeration;
import java.util.List;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import me.ixk.framework.http.SessionManager;

public class Session extends AbstractFacade {

    protected static SessionManager make() {
        return app.make(SessionManager.class);
    }

    public static void setSession(
        HttpSession session,
        org.eclipse.jetty.server.SessionManager sessionManager
    ) {
        make().setSession(session, sessionManager);
    }

    public static HttpSession getSession() {
        return make().getSession();
    }

    public static void setSession(HttpSession _session) {
        make().setSession(_session);
    }

    public static org.eclipse.jetty.server.SessionManager getSessionManager() {
        return make().getSessionManager();
    }

    public static void setSessionManager(
        org.eclipse.jetty.server.SessionManager _sessionManager
    ) {
        make().setSessionManager(_sessionManager);
    }

    public static boolean has(String name) {
        return make().has(name);
    }

    public static Object get(String name) {
        return make().get(name);
    }

    public static Object get(String name, Object _default) {
        return make().get(name, _default);
    }

    public static void put(String name, Object value) {
        make().put(name, value);
    }

    public static void forget(String name) {
        make().forget(name);
    }

    public static void forget(List<String> names) {
        make().forget(names);
    }

    public static void flush() {
        make().flush();
    }

    public static Object pull(String name, Object _default) {
        return make().pull(name, _default);
    }

    public static String token() {
        return make().token();
    }

    public static void regenerateToken() {
        make().regenerateToken();
    }

    public static long getCreationTime() {
        return make().getCreationTime();
    }

    public static String getId() {
        return make().getId();
    }

    public static long getLastAccessedTime() {
        return make().getLastAccessedTime();
    }

    public static ServletContext getServletContext() {
        return make().getServletContext();
    }

    public static void setMaxInactiveInterval(int interval) {
        make().setMaxInactiveInterval(interval);
    }

    public static int getMaxInactiveInterval() {
        return make().getMaxInactiveInterval();
    }

    public static Object getAttribute(String name) {
        return make().getAttribute(name);
    }

    public static Enumeration<String> getAttributeNames() {
        return make().getAttributeNames();
    }

    public static void setAttribute(String name, Object value) {
        make().setAttribute(name, value);
    }

    public static void removeAttribute(String name) {
        make().removeAttribute(name);
    }

    public static void invalidate() {
        make().invalidate();
    }

    public static boolean isNew() {
        return make().isNew();
    }
}
